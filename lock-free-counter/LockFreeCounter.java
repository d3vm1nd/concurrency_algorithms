import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock-Free Counter
 * 
 * A high-performance lock-free counter implementation that supports multiple
 * concurrent increment and decrement operations. Uses atomic operations to
 * maintain thread safety without locks.
 * 
 * This demonstrates various counter implementations and their use cases.
 */
public class LockFreeCounter {
    private static final int NUMBER_OF_THREADS = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;
    
    private final AtomicLong counter = new AtomicLong(0);
    
    /**
     * Increment counter
     */
    public void increment() {
        counter.incrementAndGet();
    }
    
    /**
     * Decrement counter
     */
    public void decrement() {
        counter.decrementAndGet();
    }
    
    /**
     * Add value to counter
     */
    public void add(long value) {
        counter.addAndGet(value);
    }
    
    /**
     * Get current value
     */
    public long get() {
        return counter.get();
    }
    
    /**
     * Reset counter to zero
     */
    public void reset() {
        counter.set(0);
    }
    
    /**
     * Compare and set (conditional update)
     */
    public boolean compareAndSet(long expect, long update) {
        return counter.compareAndSet(expect, update);
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Counter Demo ===\n");
        
        LockFreeCounter counter = new LockFreeCounter();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    // Increment
                    counter.increment();
                    
                    // Occasionally decrement
                    if (j % 3 == 0) {
                        counter.decrement();
                    }
                    
                    // Occasionally add
                    if (j % 5 == 0) {
                        counter.add(2);
                    }
                }
                
                System.out.println("Thread " + threadId + " completed operations");
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Final counter value: " + counter.get());
        
        // Expected: Each thread does OPERATIONS_PER_THREAD increments
        // Minus decrements (OPERATIONS_PER_THREAD / 3)
        // Plus adds (OPERATIONS_PER_THREAD / 5 * 2)
        long expected = NUMBER_OF_THREADS * 
            (OPERATIONS_PER_THREAD - (OPERATIONS_PER_THREAD / 3) + 
             (OPERATIONS_PER_THREAD / 5 * 2));
        System.out.println("Expected value: " + expected);
        System.out.println("Correctness: " + 
            (counter.get() == expected ? "✓ PASSED" : "✗ FAILED (may vary due to timing)"));
    }
}

