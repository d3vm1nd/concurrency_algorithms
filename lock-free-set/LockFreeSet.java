import java.util.concurrent.atomic.AtomicReference;

/**
 * Lock-Free Set
 * 
 * A lock-free concurrent set implementation that allows multiple threads to
 * add, remove, and check membership simultaneously. Uses a sorted linked list
 * structure with atomic operations to maintain thread safety.
 * 
 * This is a simplified implementation based on lock-free linked list principles.
 */
public class LockFreeSet<T extends Comparable<T>> {
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
    
    public LockFreeSet() {
        // Sentinel node with minimum value
        this.head = new Node<>(null);
    }
    
    /**
     * Add an element to the set
     */
    public boolean add(T value) {
        Node<T> newNode = new Node<>(value);
        
        while (true) {
            Node<T> prev = head;
            Node<T> curr = prev.next.get();
            
            // Find insertion point (sorted order)
            while (curr != null && curr.value.compareTo(value) < 0) {
                prev = curr;
                curr = curr.next.get();
            }
            
            // Check if already exists
            if (curr != null && curr.value.equals(value)) {
                return false; // Already in set
            }
            
            // Attempt insertion
            newNode.next.set(curr);
            if (prev.next.compareAndSet(curr, newNode)) {
                return true; // Successfully added
            }
            // CAS failed, retry
        }
    }
    
    /**
     * Remove an element from the set
     */
    public boolean remove(T value) {
        while (true) {
            Node<T> prev = head;
            Node<T> curr = prev.next.get();
            
            // Find the node to remove
            while (curr != null && curr.value.compareTo(value) < 0) {
                prev = curr;
                curr = curr.next.get();
            }
            
            // Check if found
            if (curr == null || !curr.value.equals(value)) {
                return false; // Not in set
            }
            
            // Attempt removal
            Node<T> next = curr.next.get();
            if (prev.next.compareAndSet(curr, next)) {
                return true; // Successfully removed
            }
            // CAS failed, retry
        }
    }
    
    /**
     * Check if element is in set
     */
    public boolean contains(T value) {
        Node<T> curr = head.next.get();
        
        while (curr != null) {
            int cmp = curr.value.compareTo(value);
            if (cmp == 0) {
                return true; // Found
            }
            if (cmp > 0) {
                return false; // Past possible location
            }
            curr = curr.next.get();
        }
        
        return false; // Not found
    }
    
    /**
     * Check if set is empty
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
        System.out.println("=== Lock-Free Set Demo ===\n");
        
        LockFreeSet<Integer> set = new LockFreeSet<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 100 + j;
                    
                    // Add
                    if (set.add(value)) {
                        System.out.println("Thread " + threadId + " added: " + value);
                    }
                    
                    // Check contains
                    if (set.contains(value)) {
                        // System.out.println("Thread " + threadId + " found: " + value);
                    }
                    
                    // Remove some values
                    if (j % 3 == 0) {
                        if (set.remove(value)) {
                            System.out.println("Thread " + threadId + " removed: " + value);
                        }
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Set size: " + set.size());
        System.out.println("Set empty: " + set.isEmpty());
    }
}

