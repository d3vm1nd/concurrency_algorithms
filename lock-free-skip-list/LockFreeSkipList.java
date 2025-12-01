import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.Random;

/**
 * Lock-Free Skip List
 * 
 * A lock-free concurrent skip list implementation that provides efficient
 * sorted data structure operations. Uses multiple levels of linked lists with
 * probabilistic balancing, allowing O(log n) average-case search, insert,
 * and delete operations.
 * 
 * This is a simplified implementation demonstrating the core concepts.
 */
public class LockFreeSkipList<T extends Comparable<T>> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 10;
    private static final int MAX_LEVEL = 4;
    private static final double PROBABILITY = 0.5;
    
    private final Node<T> head;
    private final Random random = new Random();
    
    private static class Node<T> {
        final T value;
        final AtomicMarkableReference<Node<T>>[] next;
        final int level;
        
        @SuppressWarnings("unchecked")
        Node(T value, int level) {
            this.value = value;
            this.level = level;
            this.next = new AtomicMarkableReference[level + 1];
            for (int i = 0; i <= level; i++) {
                this.next[i] = new AtomicMarkableReference<>(null, false);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public LockFreeSkipList() {
        this.head = new Node<>(null, MAX_LEVEL);
    }
    
    /**
     * Generate random level for new node
     */
    private int randomLevel() {
        int level = 0;
        while (level < MAX_LEVEL && random.nextDouble() < PROBABILITY) {
            level++;
        }
        return level;
    }
    
    /**
     * Find the node with given value or the insertion point
     */
    @SuppressWarnings("unchecked")
    private Node<T>[] find(T value) {
        Node<T>[] preds = new Node[MAX_LEVEL + 1];
        Node<T> curr = head;
        
        for (int i = MAX_LEVEL; i >= 0; i--) {
            while (true) {
                Node<T> next = curr.next[i].getReference();
                boolean[] marked = {false};
                
                if (next != null) {
                    next = next.next[i].get(marked);
                }
                
                if (next != null && next.value != null && next.value.compareTo(value) < 0) {
                    curr = next;
                } else {
                    break;
                }
            }
            preds[i] = curr;
        }
        
        return preds;
    }
    
    /**
     * Check if value exists in skip list
     */
    public boolean contains(T value) {
        Node<T>[] preds = find(value);
        Node<T> curr = preds[0].next[0].getReference();
        return curr != null && curr.value != null && curr.value.equals(value);
    }
    
    /**
     * Insert value into skip list
     */
    public boolean add(T value) {
        int topLevel = randomLevel();
        Node<T>[] preds = find(value);
        Node<T> curr = preds[0].next[0].getReference();
        
        if (curr != null && curr.value != null && curr.value.equals(value)) {
            return false; // Already exists
        }
        
        Node<T> newNode = new Node<>(value, topLevel);
        
        for (int i = 0; i <= topLevel; i++) {
            Node<T> pred = preds[i];
            Node<T> succ = pred.next[i].getReference();
            newNode.next[i].set(succ, false);
            
            if (!pred.next[i].compareAndSet(succ, newNode, false, false)) {
                // CAS failed, retry
                return add(value);
            }
        }
        
        return true;
    }
    
    /**
     * Remove value from skip list
     */
    public boolean remove(T value) {
        Node<T>[] preds = find(value);
        Node<T> curr = preds[0].next[0].getReference();
        
        if (curr == null || curr.value == null || !curr.value.equals(value)) {
            return false; // Not found
        }
        
        // Mark all levels
        for (int i = curr.level; i >= 0; i--) {
            Node<T> succ = curr.next[i].getReference();
            while (!curr.next[i].attemptMark(succ, true)) {
                succ = curr.next[i].getReference();
            }
        }
        
        // Unlink from all levels
        for (int i = curr.level; i >= 0; i--) {
            Node<T> pred = preds[i];
            Node<T> succ = curr.next[i].getReference();
            pred.next[i].compareAndSet(curr, succ, false, false);
        }
        
        return true;
    }
    
    /**
     * Get approximate size (not exact due to concurrency)
     */
    public int size() {
        int count = 0;
        Node<T> curr = head.next[0].getReference();
        while (curr != null) {
            count++;
            curr = curr.next[0].getReference();
        }
        return count;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Skip List Demo ===\n");
        
        LockFreeSkipList<Integer> skipList = new LockFreeSkipList<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 100 + j;
                    
                    // Add
                    if (skipList.add(value)) {
                        System.out.println("Thread " + threadId + " added: " + value);
                    }
                    
                    // Contains
                    if (skipList.contains(value)) {
                        // System.out.println("Thread " + threadId + " found: " + value);
                    }
                    
                    // Remove some
                    if (j % 3 == 0) {
                        if (skipList.remove(value)) {
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
        System.out.println("Skip list size: " + skipList.size());
    }
}

