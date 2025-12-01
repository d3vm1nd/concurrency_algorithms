import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Work-Stealing Queue
 * 
 * A lock-free concurrent queue optimized for producer-consumer scenarios where
 * the producer and consumer are often the same thread. Each thread has its own
 * queue, and when a thread's queue is empty, it can "steal" work from other
 * threads' queues.
 * 
 * This data structure is fundamental to work-stealing schedulers used in
 * parallel computing frameworks like Java's ForkJoinPool.
 * 
 * Key properties:
 * - Lock-free operations on the owner thread (fast path)
 * - Lock-free stealing from other threads
 * - Reduces contention compared to single shared queue
 */
public class WorkStealingQueue<T> {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int TASKS_PER_THREAD = 10;
    
    // Circular buffer for tasks
    private final AtomicReference<T>[] tasks;
    private final AtomicInteger top;  // Owner thread's index (grows up)
    private final AtomicInteger bottom; // Owner thread's index (grows down)
    private final int capacity;
    private final int mask; // For modulo operation (capacity must be power of 2)
    
    @SuppressWarnings("unchecked")
    public WorkStealingQueue(int capacity) {
        // Capacity must be power of 2 for efficient modulo
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("Capacity must be a power of 2");
        }
        this.capacity = capacity;
        this.mask = capacity - 1;
        this.tasks = new AtomicReference[capacity];
        for (int i = 0; i < capacity; i++) {
            this.tasks[i] = new AtomicReference<>(null);
        }
        this.top = new AtomicInteger(0);
        this.bottom = new AtomicInteger(0);
    }
    
    /**
     * Push task to the bottom (owner thread operation)
     * This is the fast path - no synchronization needed for owner
     */
    public void push(T task) {
        int b = bottom.get();
        int t = top.get();
        
        // Check if queue is full (with some slack for concurrent operations)
        if (b - t >= capacity - 1) {
            throw new IllegalStateException("Queue is full");
        }
        
        tasks[b & mask].set(task);
        bottom.set(b + 1);
    }
    
    /**
     * Pop task from the bottom (owner thread operation)
     * This is the fast path - optimized for owner thread
     */
    public T pop() {
        int b = bottom.get();
        if (b == 0) {
            b = bottom.get(); // Re-read if was 0
        }
        bottom.set(b - 1);
        
        int t = top.get();
        if (b - 1 < t) {
            // Queue is empty
            bottom.set(t);
            return null;
        }
        
        T task = tasks[(b - 1) & mask].get();
        if (b - 1 > t) {
            // More than one element, safe to return
            return task;
        }
        
        // Last element - need to coordinate with stealers
        if (!top.compareAndSet(t, t + 1)) {
            // Someone stole it
            task = null;
        }
        bottom.set(t + 1);
        return task;
    }
    
    /**
     * Steal task from the top (other thread operation)
     * This is the stealing path - uses CAS for synchronization
     */
    public T steal() {
        int t = top.get();
        int b = bottom.get();
        
        if (b <= t) {
            // Queue appears empty
            return null;
        }
        
        T task = tasks[t & mask].get();
        if (!top.compareAndSet(t, t + 1)) {
            // Failed to steal (race condition)
            return null;
        }
        
        return task;
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        int t = top.get();
        int b = bottom.get();
        return b <= t;
    }
    
    /**
     * Get approximate size (not exact due to concurrency)
     */
    public int size() {
        int t = top.get();
        int b = bottom.get();
        return Math.max(0, b - t);
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Work-Stealing Queue Demo ===\n");
        
        // Create work-stealing queues for each thread
        @SuppressWarnings("unchecked")
        WorkStealingQueue<Integer>[] queues = new WorkStealingQueue[NUMBER_OF_THREADS];
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            queues[i] = new WorkStealingQueue<>(32); // Power of 2 capacity
        }
        
        // Create threads that produce and consume work
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            final WorkStealingQueue<Integer> myQueue = queues[i];
            final WorkStealingQueue<Integer>[] allQueues = queues;
            
            threads[i] = Thread.ofVirtual().start(() -> {
                // Phase 1: Produce tasks to own queue
                for (int j = 0; j < TASKS_PER_THREAD; j++) {
                    int task = threadId * 1000 + j;
                    myQueue.push(task);
                    System.out.println("Thread " + threadId + " pushed task: " + task);
                }
                
                // Phase 2: Process tasks (pop from own queue, steal from others)
                int processed = 0;
                while (processed < TASKS_PER_THREAD) {
                    // Try to pop from own queue first
                    Integer task = myQueue.pop();
                    
                    if (task == null) {
                        // Own queue empty, try to steal from others
                        for (int k = 0; k < NUMBER_OF_THREADS; k++) {
                            if (k != threadId) {
                                task = allQueues[k].steal();
                                if (task != null) {
                                    System.out.println("Thread " + threadId + 
                                        " stole task: " + task + " from queue " + k);
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println("Thread " + threadId + 
                            " popped own task: " + task);
                    }
                    
                    if (task != null) {
                        // Process the task
                        processTask(threadId, task);
                        processed++;
                        completedTasks.incrementAndGet();
                    } else {
                        // No work available, yield
                        Thread.yield();
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Total tasks completed: " + completedTasks.get());
        System.out.println("Expected tasks: " + (NUMBER_OF_THREADS * TASKS_PER_THREAD));
        System.out.println("Correctness: " + 
            (completedTasks.get() == NUMBER_OF_THREADS * TASKS_PER_THREAD ? 
                "✓ PASSED" : "✗ FAILED"));
    }
    
    private static void processTask(int threadId, int task) {
        // Simulate task processing
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

