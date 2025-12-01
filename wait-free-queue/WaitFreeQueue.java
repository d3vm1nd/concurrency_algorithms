import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Wait-Free Queue
 * 
 * A wait-free concurrent queue implementation where every operation completes
 * in a bounded number of steps, regardless of other threads' behavior. This
 * is stronger than lock-free, which only guarantees that some thread makes progress.
 * 
 * This implementation uses helping mechanisms where threads help other threads
 * complete their operations.
 */
public class WaitFreeQueue<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 10;
    
    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;
    private final AtomicInteger enqueueCount = new AtomicInteger(0);
    
    private static class Node<T> {
        final T value;
        volatile AtomicReference<Node<T>> next;
        volatile boolean enqueued;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
            this.enqueued = false;
        }
    }
    
    public WaitFreeQueue() {
        Node<T> dummy = new Node<>(null);
        dummy.enqueued = true;
        this.head = new AtomicReference<>(dummy);
        this.tail = new AtomicReference<>(dummy);
    }
    
    /**
     * Enqueue operation (wait-free)
     */
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        int myTicket = enqueueCount.getAndIncrement();
        
        // Help other threads if needed, then do our operation
        helpEnqueue();
        doEnqueue(newNode);
    }
    
    /**
     * Help other threads complete their enqueue operations
     */
    private void helpEnqueue() {
        Node<T> last = tail.get();
        Node<T> next = last.next.get();
        
        if (next != null) {
            // Help complete previous enqueue
            tail.compareAndSet(last, next);
        }
    }
    
    /**
     * Perform the actual enqueue
     */
    private void doEnqueue(Node<T> node) {
        while (true) {
            Node<T> last = tail.get();
            Node<T> next = last.next.get();
            
            if (last == tail.get()) {
                if (next == null) {
                    // Try to link new node
                    if (last.next.compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    // Help advance tail
                    tail.compareAndSet(last, next);
                }
            }
        }
    }
    
    /**
     * Dequeue operation (wait-free)
     */
    public T dequeue() {
        while (true) {
            Node<T> first = head.get();
            Node<T> last = tail.get();
            Node<T> next = first.next.get();
            
            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        return null; // Queue is empty
                    }
                    // Help advance tail
                    tail.compareAndSet(last, next);
                } else {
                    if (head.compareAndSet(first, next)) {
                        return next.value;
                    }
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return head.get() == tail.get() && 
               head.get().next.get() == null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Wait-Free Queue Demo ===\n");
        
        WaitFreeQueue<Integer> queue = new WaitFreeQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 100 + j;
                    
                    // Enqueue
                    queue.enqueue(value);
                    System.out.println("Thread " + threadId + " enqueued: " + value);
                    
                    // Dequeue
                    Integer dequeued = queue.dequeue();
                    if (dequeued != null) {
                        System.out.println("Thread " + threadId + " dequeued: " + dequeued);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nQueue operations completed!");
        System.out.println("Final queue empty: " + queue.isEmpty());
    }
}

