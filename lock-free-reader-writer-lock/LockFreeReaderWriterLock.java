import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock-Free Reader-Writer Lock
 * 
 * A reader-writer lock implementation using atomic operations. Allows multiple
 * readers or a single writer, but uses lock-free mechanisms instead of traditional
 * blocking locks. This is a simplified implementation.
 * 
 * Note: True lock-free reader-writer locks are complex. This demonstrates the concept.
 */
public class LockFreeReaderWriterLock {
    private static final int NUMBER_OF_READERS = 10;
    private static final int NUMBER_OF_WRITERS = 2;
    private static final int READ_ITERATIONS = 50;
    private static final int WRITE_ITERATIONS = 10;
    
    // State: bit 0 = writer present, bits 1-31 = reader count
    private final AtomicInteger state = new AtomicInteger(0);
    private static final int WRITER_BIT = 1;
    private static final int READER_COUNT_MASK = 0xFFFFFFFE;
    
    /**
     * Acquire read lock
     */
    public void readLock() {
        while (true) {
            int current = state.get();
            
            // Check if writer is present
            if ((current & WRITER_BIT) != 0) {
                Thread.onSpinWait();
                continue;
            }
            
            // Try to increment reader count
            int newState = current + 2; // Increment by 2 (skip writer bit)
            if (state.compareAndSet(current, newState)) {
                return; // Success
            }
        }
    }
    
    /**
     * Release read lock
     */
    public void readUnlock() {
        state.addAndGet(-2); // Decrement by 2
    }
    
    /**
     * Acquire write lock
     */
    public void writeLock() {
        while (true) {
            int current = state.get();
            
            // Check if writer or readers present
            if (current != 0) {
                Thread.onSpinWait();
                continue;
            }
            
            // Try to set writer bit
            if (state.compareAndSet(0, WRITER_BIT)) {
                return; // Success
            }
        }
    }
    
    /**
     * Release write lock
     */
    public void writeUnlock() {
        if (!state.compareAndSet(WRITER_BIT, 0)) {
            throw new IllegalStateException("Write lock not held");
        }
    }
    
    /**
     * Get current state (for debugging)
     */
    public int getState() {
        return state.get();
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Reader-Writer Lock Demo ===\n");
        
        LockFreeReaderWriterLock rwLock = new LockFreeReaderWriterLock();
        AtomicInteger sharedData = new AtomicInteger(0);
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);
        
        // Create reader threads
        Thread[] readers = new Thread[NUMBER_OF_READERS];
        for (int i = 0; i < NUMBER_OF_READERS; i++) {
            final int readerId = i;
            readers[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < READ_ITERATIONS; j++) {
                    rwLock.readLock();
                    try {
                        int value = sharedData.get();
                        readCount.incrementAndGet();
                        
                        if (j % 10 == 0) {
                            System.out.println("Reader " + readerId + " read: " + value);
                        }
                    } finally {
                        rwLock.readUnlock();
                    }
                    
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Create writer threads
        Thread[] writers = new Thread[NUMBER_OF_WRITERS];
        for (int i = 0; i < NUMBER_OF_WRITERS; i++) {
            final int writerId = i;
            writers[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < WRITE_ITERATIONS; j++) {
                    rwLock.writeLock();
                    try {
                        int newValue = writerId * 1000 + j;
                        sharedData.set(newValue);
                        writeCount.incrementAndGet();
                        System.out.println("Writer " + writerId + " wrote: " + newValue);
                    } finally {
                        rwLock.writeUnlock();
                    }
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Wait for all threads
        for (Thread reader : readers) {
            reader.join();
        }
        for (Thread writer : writers) {
            writer.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Total reads: " + readCount.get());
        System.out.println("Total writes: " + writeCount.get());
        System.out.println("Final data value: " + sharedData.get());
    }
}

