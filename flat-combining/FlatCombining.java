import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Flat Combining
 * 
 * A synchronization technique where one thread (the combiner) executes operations
 * on behalf of other threads, reducing contention and improving cache locality.
 * Threads publish their operations to a shared queue, and a combiner thread processes
 * them in batch.
 * 
 * Key features:
 * - Reduces contention by batching operations
 * - Improves cache locality
 * - One thread does the work (combiner)
 * - Other threads publish operations
 */
public class FlatCombining {
    private static final int NUMBER_OF_THREADS = 10;
    private static final int OPERATIONS_PER_THREAD = 20;
    
    private final Queue<Operation> operationQueue = new ConcurrentLinkedQueue<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicReference<Thread> combiner = new AtomicReference<>(null);
    
    private static class Operation {
        final int threadId;
        final OperationType type;
        volatile int result;
        volatile boolean completed = false;
        
        Operation(int threadId, OperationType type) {
            this.threadId = threadId;
            this.type = type;
        }
    }
    
    private enum OperationType {
        INCREMENT,
        DECREMENT,
        GET
    }
    
    /**
     * Try to become the combiner
     */
    private boolean tryCombine() {
        Thread currentThread = Thread.currentThread();
        
        if (combiner.compareAndSet(null, currentThread)) {
            // We are the combiner
            try {
                combineOperations();
            } finally {
                combiner.set(null);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Combine and execute operations from the queue
     */
    private void combineOperations() {
        int batchSize = 0;
        final int MAX_BATCH = 100;
        
        while (batchSize < MAX_BATCH) {
            Operation op = operationQueue.poll();
            if (op == null) {
                break; // No more operations
            }
            
            // Execute the operation
            switch (op.type) {
                case INCREMENT:
                    op.result = counter.incrementAndGet();
                    break;
                case DECREMENT:
                    op.result = counter.decrementAndGet();
                    break;
                case GET:
                    op.result = counter.get();
                    break;
            }
            
            op.completed = true;
            batchSize++;
        }
    }
    
    /**
     * Publish an operation and wait for completion
     */
    private int publishAndWait(OperationType type) {
        Operation op = new Operation(
            (int) Thread.currentThread().getId(), type);
        
        // Try to become combiner first
        if (!tryCombine()) {
            // Not the combiner, publish operation
            operationQueue.offer(op);
            
            // Try to help by becoming combiner
            tryCombine();
            
            // Wait for completion
            while (!op.completed) {
                Thread.onSpinWait();
            }
        } else {
            // We became combiner, execute directly
            switch (type) {
                case INCREMENT:
                    op.result = counter.incrementAndGet();
                    break;
                case DECREMENT:
                    op.result = counter.decrementAndGet();
                    break;
                case GET:
                    op.result = counter.get();
                    break;
            }
            op.completed = true;
        }
        
        return op.result;
    }
    
    public void increment() {
        publishAndWait(OperationType.INCREMENT);
    }
    
    public void decrement() {
        publishAndWait(OperationType.DECREMENT);
    }
    
    public int get() {
        return publishAndWait(OperationType.GET);
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Flat Combining Demo ===\n");
        
        FlatCombining fc = new FlatCombining();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    fc.increment();
                    
                    if (j % 5 == 0) {
                        int value = fc.get();
                        System.out.println("Thread " + threadId + 
                            " sees counter: " + value);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Final counter value: " + fc.get());
        System.out.println("Expected: " + (NUMBER_OF_THREADS * OPERATIONS_PER_THREAD));
        System.out.println("Correctness: " + 
            (fc.get() == NUMBER_OF_THREADS * OPERATIONS_PER_THREAD ? 
                "✓ PASSED" : "✗ FAILED"));
    }
}

