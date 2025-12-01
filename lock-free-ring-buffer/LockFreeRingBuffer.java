import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Lock-Free Ring Buffer (Circular Buffer)
 * 
 * A bounded lock-free concurrent ring buffer that supports multiple producers
 * and consumers. Uses atomic operations to maintain thread safety without locks.
 * 
 * Key features:
 * - Fixed capacity (power of 2 for efficient modulo)
 * - Lock-free operations
 * - Multiple producers and consumers
 * - Bounded buffer (backpressure)
 */
public class LockFreeRingBuffer<T> {
    private static final int NUMBER_OF_PRODUCERS = 3;
    private static final int NUMBER_OF_CONSUMERS = 2;
    private static final int ITEMS_PER_PRODUCER = 20;
    
    private final AtomicReferenceArray<T> buffer;
    private final AtomicInteger writeIndex;
    private final AtomicInteger readIndex;
    private final int capacity;
    private final int mask; // For efficient modulo (capacity must be power of 2)
    
    @SuppressWarnings("unchecked")
    public LockFreeRingBuffer(int capacity) {
        // Capacity must be power of 2
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("Capacity must be a power of 2");
        }
        this.capacity = capacity;
        this.mask = capacity - 1;
        this.buffer = new AtomicReferenceArray<>(capacity);
        this.writeIndex = new AtomicInteger(0);
        this.readIndex = new AtomicInteger(0);
    }
    
    /**
     * Enqueue an item (producer operation)
     * Returns true if successful, false if buffer is full
     */
    public boolean enqueue(T item) {
        int currentWrite;
        int nextWrite;
        
        do {
            currentWrite = writeIndex.get();
            nextWrite = (currentWrite + 1) & mask;
            
            // Check if buffer is full
            if (nextWrite == readIndex.get()) {
                return false; // Buffer is full
            }
        } while (!writeIndex.compareAndSet(currentWrite, nextWrite));
        
        // Write the item
        buffer.set(currentWrite, item);
        return true;
    }
    
    /**
     * Dequeue an item (consumer operation)
     * Returns null if buffer is empty
     */
    public T dequeue() {
        int currentRead;
        int nextRead;
        T item;
        
        do {
            currentRead = readIndex.get();
            nextRead = (currentRead + 1) & mask;
            
            // Check if buffer is empty
            if (currentRead == writeIndex.get()) {
                return null; // Buffer is empty
            }
            
            // Read the item
            item = buffer.get(currentRead);
            
            if (item == null) {
                // Item not yet written by producer, retry
                continue;
            }
        } while (!readIndex.compareAndSet(currentRead, nextRead));
        
        // Clear the slot
        buffer.set(currentRead, null);
        return item;
    }
    
    /**
     * Get approximate size (not exact due to concurrency)
     */
    public int size() {
        int write = writeIndex.get();
        int read = readIndex.get();
        if (write >= read) {
            return write - read;
        } else {
            return (capacity - read) + write;
        }
    }
    
    public boolean isEmpty() {
        return readIndex.get() == writeIndex.get();
    }
    
    public boolean isFull() {
        int write = writeIndex.get();
        int read = readIndex.get();
        int nextWrite = (write + 1) & mask;
        return nextWrite == read;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Ring Buffer Demo ===\n");
        
        LockFreeRingBuffer<Integer> buffer = new LockFreeRingBuffer<>(32);
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        
        // Create producer threads
        Thread[] producers = new Thread[NUMBER_OF_PRODUCERS];
        for (int i = 0; i < NUMBER_OF_PRODUCERS; i++) {
            final int producerId = i;
            producers[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < ITEMS_PER_PRODUCER; j++) {
                    int value = producerId * 1000 + j;
                    
                    // Try to enqueue with retry
                    while (!buffer.enqueue(value)) {
                        Thread.onSpinWait(); // Buffer full, wait
                    }
                    
                    produced.incrementAndGet();
                    System.out.println("Producer " + producerId + " enqueued: " + value);
                    
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Create consumer threads
        Thread[] consumers = new Thread[NUMBER_OF_CONSUMERS];
        for (int i = 0; i < NUMBER_OF_CONSUMERS; i++) {
            final int consumerId = i;
            consumers[i] = Thread.ofVirtual().start(() -> {
                while (produced.get() < NUMBER_OF_PRODUCERS * ITEMS_PER_PRODUCER || 
                       !buffer.isEmpty()) {
                    Integer value = buffer.dequeue();
                    
                    if (value != null) {
                        consumed.incrementAndGet();
                        System.out.println("Consumer " + consumerId + " dequeued: " + value);
                    } else {
                        // Buffer empty, yield
                        Thread.yield();
                    }
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Wait for producers
        for (Thread producer : producers) {
            producer.join();
        }
        
        // Wait a bit for consumers to finish
        Thread.sleep(100);
        
        // Wait for consumers
        for (Thread consumer : consumers) {
            consumer.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Total produced: " + produced.get());
        System.out.println("Total consumed: " + consumed.get());
        System.out.println("Buffer size: " + buffer.size());
        System.out.println("Buffer empty: " + buffer.isEmpty());
        System.out.println("Correctness: " + 
            (produced.get() == consumed.get() ? "✓ PASSED" : "✗ FAILED"));
    }
}

