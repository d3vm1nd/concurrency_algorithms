import java.util.concurrent.atomic.AtomicReference;

/**
 * Lock-Free Priority Queue
 * 
 * A lock-free concurrent priority queue implementation using a sorted linked list.
 * Elements are maintained in priority order, with the highest priority element
 * at the head. Supports concurrent insert and remove operations.
 * 
 * This is a simplified implementation. Real-world implementations often use
 * more sophisticated structures like skip lists or heaps.
 */
public class LockFreePriorityQueue<T extends Comparable<T>> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 10;
    
    private final Node<T> head;
    
    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }
    
    public LockFreePriorityQueue() {
        // Sentinel node with minimum value
        this.head = new Node<>(null);
    }
    
    /**
     * Insert an element (maintains priority order)
     */
    public void insert(T value) {
        Node<T> newNode = new Node<>(value);
        
        while (true) {
            Node<T> prev = head;
            Node<T> curr = prev.next.get();
            
            // Find insertion point (sorted by priority, highest first)
            while (curr != null && curr.value.compareTo(value) > 0) {
                prev = curr;
                curr = curr.next.get();
            }
            
            // Attempt insertion
            newNode.next.set(curr);
            if (prev.next.compareAndSet(curr, newNode)) {
                return; // Success
            }
            // CAS failed, retry
        }
    }
    
    /**
     * Remove and return the highest priority element
     */
    public T removeMin() {
        while (true) {
            Node<T> first = head.next.get();
            
            if (first == null) {
                return null; // Queue is empty
            }
            
            Node<T> next = first.next.get();
            if (head.next.compareAndSet(first, next)) {
                return first.value; // Success
            }
            // CAS failed, retry
        }
    }
    
    /**
     * Peek at the highest priority element without removing
     */
    public T peek() {
        Node<T> first = head.next.get();
        return first != null ? first.value : null;
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return head.next.get() == null;
    }
    
    /**
     * Get approximate size (not exact due to concurrency)
     */
    public int size() {
        int count = 0;
        Node<T> curr = head.next.get();
        while (curr != null) {
            count++;
            curr = curr.next.get();
        }
        return count;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Priority Queue Demo ===\n");
        
        LockFreePriorityQueue<Integer> queue = new LockFreePriorityQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    // Insert with priority (lower number = higher priority)
                    int priority = threadId * 100 + j;
                    queue.insert(priority);
                    System.out.println("Thread " + threadId + " inserted: " + priority);
                    
                    // Remove min
                    Integer min = queue.removeMin();
                    if (min != null) {
                        System.out.println("Thread " + threadId + " removed min: " + min);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Queue size: " + queue.size());
        System.out.println("Queue empty: " + queue.isEmpty());
        
        // Show remaining elements in priority order
        System.out.println("\nRemaining elements (in priority order):");
        Integer elem;
        while ((elem = queue.removeMin()) != null) {
            System.out.println("  " + elem);
        }
    }
}

