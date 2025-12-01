import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

/**
 * Timeout Lock
 * 
 * A lock implementation that supports timeout - threads can attempt to acquire
 * the lock with a maximum wait time. If the lock cannot be acquired within the
 * timeout period, the operation fails instead of blocking indefinitely.
 * 
 * Key features:
 * - Timeout support for lock acquisition
 * - Prevents indefinite blocking
 * - Useful for avoiding deadlocks
 * - Graceful failure when lock unavailable
 */
public class TimeoutLock {
    private static final int NUMBER_OF_THREADS = 5;
    
    private final AtomicBoolean locked = new AtomicBoolean(false);
    
    /**
     * Try to acquire lock with timeout
     * 
     * @param timeout Timeout duration
     * @param unit Time unit
     * @return true if lock acquired, false if timeout
     */
    public boolean tryLock(long timeout, TimeUnit unit) {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        
        while (System.nanoTime() < deadline) {
            if (locked.compareAndSet(false, true)) {
                return true; // Lock acquired
            }
            
            // Brief yield to avoid busy-waiting
            Thread.onSpinWait();
        }
        
        return false; // Timeout
    }
    
    /**
     * Try to acquire lock immediately (non-blocking)
     */
    public boolean tryLock() {
        return locked.compareAndSet(false, true);
    }
    
    /**
     * Acquire lock (blocking, no timeout)
     */
    public void lock() {
        while (!locked.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }
    }
    
    /**
     * Release the lock
     */
    public void unlock() {
        if (!locked.compareAndSet(true, false)) {
            throw new IllegalStateException("Lock not held by current thread");
        }
    }
    
    /**
     * Check if lock is held
     */
    public boolean isLocked() {
        return locked.get();
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Timeout Lock Demo ===\n");
        
        TimeoutLock lock = new TimeoutLock();
        AtomicInteger acquiredCount = new AtomicInteger(0);
        AtomicInteger timeoutCount = new AtomicInteger(0);
        
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                // Try to acquire lock with timeout
                boolean acquired = lock.tryLock(100, TimeUnit.MILLISECONDS);
                
                if (acquired) {
                    acquiredCount.incrementAndGet();
                    System.out.println("Thread " + threadId + " acquired lock");
                    
                    try {
                        // Hold lock for some time
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        lock.unlock();
                        System.out.println("Thread " + threadId + " released lock");
                    }
                } else {
                    timeoutCount.incrementAndGet();
                    System.out.println("Thread " + threadId + " timed out waiting for lock");
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Locks acquired: " + acquiredCount.get());
        System.out.println("Timeouts: " + timeoutCount.get());
    }
}

