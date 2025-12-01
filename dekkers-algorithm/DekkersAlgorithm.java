import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Dekker's Algorithm - A classic software-only solution for mutual exclusion
 * between exactly two processes, developed by Dutch mathematician Th. J. Dekker.
 * 
 * This algorithm uses flags and a turn variable to coordinate access to shared
 * resources without hardware support, similar to Peterson's Algorithm but with
 * a different approach to ensuring mutual exclusion.
 */
public class DekkersAlgorithm {
    private static final AtomicBoolean flag0 = new AtomicBoolean(false);
    private static final AtomicBoolean flag1 = new AtomicBoolean(false);
    private static final AtomicInteger turn = new AtomicInteger(0);
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private static final int ITERATIONS = 5;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Dekker's Algorithm Demo ===");
        System.out.println("Demonstrating mutual exclusion between 2 threads\n");
        
        Thread thread0 = Thread.ofVirtual().start(() -> process(0));
        Thread thread1 = Thread.ofVirtual().start(() -> process(1));
        
        thread0.join();
        thread1.join();
        
        System.out.println("\n=== Results ===");
        System.out.println("Expected counter value: " + (ITERATIONS * 2));
        System.out.println("Actual counter value: " + sharedCounter.get());
        System.out.println("Algorithm correctness: " + 
            (sharedCounter.get() == ITERATIONS * 2 ? "✓ PASSED" : "✗ FAILED"));
    }
    
    /**
     * Process execution for a thread
     */
    private static void process(int id) {
        for (int i = 0; i < ITERATIONS; i++) {
            enterCriticalSection(id);
            criticalSection(id, i);
            exitCriticalSection(id);
            nonCriticalSection(id, i);
        }
    }
    
    /**
     * Enter critical section using Dekker's Algorithm
     * 
     * Algorithm steps:
     * 1. Set own flag to true (indicate desire to enter)
     * 2. While other thread's flag is true:
     *    - If it's not our turn, wait
     *    - Otherwise, set our flag to false and wait for our turn
     *    - Then set our flag back to true
     */
    private static void enterCriticalSection(int id) {
        int other = 1 - id;
        AtomicBoolean myFlag = (id == 0) ? flag0 : flag1;
        AtomicBoolean otherFlag = (other == 0) ? flag0 : flag1;
        
        myFlag.set(true);
        
        while (otherFlag.get()) {
            if (turn.get() != id) {
                // Not our turn, wait
                myFlag.set(false);
                while (turn.get() != id) {
                    Thread.onSpinWait();
                }
                myFlag.set(true);
            }
        }
    }
    
    /**
     * Exit critical section
     */
    private static void exitCriticalSection(int id) {
        int other = 1 - id;
        AtomicBoolean myFlag = (id == 0) ? flag0 : flag1;
        
        myFlag.set(false);
        turn.set(other); // Give turn to other thread
    }
    
    /**
     * Critical section - protected shared resource access
     */
    private static void criticalSection(int id, int iteration) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + id + " in critical section (iteration " + 
            iteration + "), counter: " + value);
        
        // Simulate some work
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Non-critical section
     */
    private static void nonCriticalSection(int id, int iteration) {
        System.out.println("Thread " + id + " in non-critical section (iteration " + 
            iteration + ")");
    }
}

