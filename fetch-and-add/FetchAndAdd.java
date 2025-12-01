import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fetch-and-Add Algorithm - A fundamental atomic operation for concurrent programming.
 * 
 * Fetch-and-Add atomically reads a value from memory, adds a constant to it,
 * and writes the result back, returning the original value. This operation is
 * essential for implementing counters, accumulators, and other concurrent data structures.
 * 
 * In Java, this is typically implemented using AtomicInteger.addAndGet() or
 * getAndAdd() methods, which provide the fetch-and-add semantics.
 */
public class FetchAndAdd {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int INCREMENTS_PER_THREAD = 100;
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private static final AtomicInteger operationCounter = new AtomicInteger(0);
    
    /**
     * Fetch-and-Add operation: atomically add delta to value and return old value
     * 
     * @param value The atomic integer to modify
     * @param delta The value to add
     * @return The value before the addition
     */
    public static int fetchAndAdd(AtomicInteger value, int delta) {
        return value.getAndAdd(delta);
    }
    
    /**
     * Fetch-and-Add operation: atomically add delta to value and return new value
     * 
     * @param value The atomic integer to modify
     * @param delta The value to add
     * @return The value after the addition
     */
    public static int addAndFetch(AtomicInteger value, int delta) {
        return value.addAndGet(delta);
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Fetch-and-Add Algorithm Demo ===");
        System.out.println("Demonstrating atomic increment operations\n");
        
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> runThreadProcess(threadId));
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        int expectedValue = NUMBER_OF_THREADS * INCREMENTS_PER_THREAD;
        System.out.println("Expected counter value: " + expectedValue);
        System.out.println("Actual counter value: " + sharedCounter.get());
        System.out.println("Total operations: " + operationCounter.get());
        System.out.println("Algorithm correctness: " + 
            (sharedCounter.get() == expectedValue ? "✓ PASSED" : "✗ FAILED"));
    }
    
    /**
     * Thread process that performs fetch-and-add operations
     */
    private static void runThreadProcess(int threadId) {
        for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
            // Method 1: getAndAdd - returns old value (fetch-and-add)
            int oldValue = fetchAndAdd(sharedCounter, 1);
            operationCounter.incrementAndGet();
            
            if (i % 20 == 0) {
                System.out.println("Thread " + threadId + " incremented: " + 
                    oldValue + " -> " + (oldValue + 1));
            }
        }
    }
    
    /**
     * Example: Using fetch-and-add to implement a ticket-based system
     */
    public static class TicketSystem {
        private final AtomicInteger nextTicket = new AtomicInteger(0);
        private final AtomicInteger currentServing = new AtomicInteger(0);
        
        /**
         * Get a ticket number
         */
        public int takeTicket() {
            return fetchAndAdd(nextTicket, 1);
        }
        
        /**
         * Check if it's your turn
         */
        public boolean isMyTurn(int ticket) {
            return currentServing.get() == ticket;
        }
        
        /**
         * Serve next customer
         */
        public void serveNext() {
            int served = fetchAndAdd(currentServing, 1);
            System.out.println("Serving ticket: " + served);
        }
    }
    
    /**
     * Example: Using fetch-and-add for load balancing
     */
    public static class LoadBalancer {
        private final AtomicInteger currentIndex = new AtomicInteger(0);
        private final int[] servers;
        
        public LoadBalancer(int numServers) {
            this.servers = new int[numServers];
        }
        
        /**
         * Get next server using round-robin with fetch-and-add
         */
        public int getNextServer() {
            int index = fetchAndAdd(currentIndex, 1);
            return index % servers.length;
        }
    }
}

