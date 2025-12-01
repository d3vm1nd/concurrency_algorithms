import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock-Free Vector (Resizable Array)
 * 
 * A lock-free concurrent vector implementation that supports dynamic resizing.
 * Uses atomic operations to maintain thread safety without locks. This is a
 * simplified implementation demonstrating the concept.
 * 
 * Note: Full lock-free resizable arrays are complex. This demonstrates the
 * basic approach.
 */
public class LockFreeVector<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 20;
    private static final int INITIAL_CAPACITY = 16;
    
    private volatile AtomicReferenceArray<T> array;
    private final AtomicInteger size = new AtomicInteger(0);
    
    @SuppressWarnings("unchecked")
    public LockFreeVector() {
        this.array = new AtomicReferenceArray<>(INITIAL_CAPACITY);
    }
    
    /**
     * Get element at index
     */
    public T get(int index) {
        if (index < 0 || index >= size.get()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return array.get(index);
    }
    
    /**
     * Set element at index
     */
    public void set(int index, T value) {
        if (index < 0 || index >= size.get()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        array.set(index, value);
    }
    
    /**
     * Add element to the end (simplified - not fully lock-free for resizing)
     */
    public void add(T value) {
        int currentSize;
        int newSize;
        
        do {
            currentSize = size.get();
            newSize = currentSize + 1;
            
            // Check if resize needed
            if (newSize > array.length()) {
                resize();
            }
        } while (!size.compareAndSet(currentSize, newSize));
        
        // Set the value
        array.set(currentSize, value);
    }
    
    /**
     * Resize the array (simplified implementation)
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        AtomicReferenceArray<T> oldArray = array;
        int oldCapacity = oldArray.length();
        int newCapacity = oldCapacity * 2;
        
        AtomicReferenceArray<T> newArray = new AtomicReferenceArray<>(newCapacity);
        
        // Copy elements
        for (int i = 0; i < oldCapacity; i++) {
            newArray.set(i, oldArray.get(i));
        }
        
        // Atomically update array reference
        array = newArray;
    }
    
    /**
     * Get current size
     */
    public int size() {
        return size.get();
    }
    
    /**
     * Get capacity
     */
    public int capacity() {
        return array.length();
    }
    
    /**
     * Check if vector is empty
     */
    public boolean isEmpty() {
        return size.get() == 0;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Vector Demo ===\n");
        
        LockFreeVector<Integer> vector = new LockFreeVector<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 1000 + j;
                    
                    // Add element
                    vector.add(value);
                    System.out.println("Thread " + threadId + " added: " + value);
                    
                    // Read element (if exists)
                    if (vector.size() > 0) {
                        int index = (threadId * OPERATIONS_PER_THREAD + j) % vector.size();
                        Integer read = vector.get(index);
                        // System.out.println("Thread " + threadId + " read[" + index + "]: " + read);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Vector size: " + vector.size());
        System.out.println("Vector capacity: " + vector.capacity());
        System.out.println("Vector empty: " + vector.isEmpty());
        
        // Show some elements
        System.out.println("\nFirst 10 elements:");
        for (int i = 0; i < Math.min(10, vector.size()); i++) {
            System.out.println("  [" + i + "] = " + vector.get(i));
        }
    }
}

