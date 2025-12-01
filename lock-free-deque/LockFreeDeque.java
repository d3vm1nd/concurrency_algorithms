import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lock-Free Deque (Double-Ended Queue)
 * 
 * A lock-free concurrent double-ended queue that allows operations on both ends
 * (push/pop from left and right). This implementation uses a doubly-linked list
 * with atomic operations to maintain thread safety without locks.
 * 
 * Key features:
 * - Lock-free operations on both ends
 * - Supports concurrent push/pop from left and right
 * - Uses CAS operations for thread safety
 */
public class LockFreeDeque<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 4;
    
    private final AtomicReference<Node<T>> leftSentinel;
    private final AtomicReference<Node<T>> rightSentinel;
    
    private static class Node<T> {
        final T value;
        volatile AtomicReference<Node<T>> prev;
        volatile AtomicReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.prev = new AtomicReference<>(null);
            this.next = new AtomicReference<>(null);
        }
    }
    
    public LockFreeDeque() {
        Node<T> left = new Node<>(null);
        Node<T> right = new Node<>(null);
        left.next.set(right);
        right.prev.set(left);
        this.leftSentinel = new AtomicReference<>(left);
        this.rightSentinel = new AtomicReference<>(right);
    }
    
    /**
     * Push element to the left end
     */
    public void pushLeft(T item) {
        Node<T> newNode = new Node<>(item);
        
        while (true) {
            Node<T> left = leftSentinel.get();
            Node<T> next = left.next.get();
            
            if (left != leftSentinel.get()) {
                continue; // Retry if sentinel changed
            }
            
            newNode.next.set(next);
            newNode.prev.set(left);
            
            if (next.prev.compareAndSet(left, newNode)) {
                left.next.compareAndSet(next, newNode);
                return;
            }
        }
    }
    
    /**
     * Push element to the right end
     */
    public void pushRight(T item) {
        Node<T> newNode = new Node<>(item);
        
        while (true) {
            Node<T> right = rightSentinel.get();
            Node<T> prev = right.prev.get();
            
            if (right != rightSentinel.get()) {
                continue; // Retry if sentinel changed
            }
            
            newNode.prev.set(prev);
            newNode.next.set(right);
            
            if (prev.next.compareAndSet(right, newNode)) {
                right.prev.compareAndSet(prev, newNode);
                return;
            }
        }
    }
    
    /**
     * Pop element from the left end
     */
    public T popLeft() {
        while (true) {
            Node<T> left = leftSentinel.get();
            Node<T> next = left.next.get();
            
            if (left != leftSentinel.get()) {
                continue;
            }
            
            if (next == rightSentinel.get()) {
                return null; // Empty deque
            }
            
            Node<T> nextNext = next.next.get();
            if (nextNext == null) {
                continue; // Retry
            }
            
            if (left.next.compareAndSet(next, nextNext)) {
                nextNext.prev.compareAndSet(next, left);
                return next.value;
            }
        }
    }
    
    /**
     * Pop element from the right end
     */
    public T popRight() {
        while (true) {
            Node<T> right = rightSentinel.get();
            Node<T> prev = right.prev.get();
            
            if (right != rightSentinel.get()) {
                continue;
            }
            
            if (prev == leftSentinel.get()) {
                return null; // Empty deque
            }
            
            Node<T> prevPrev = prev.prev.get();
            if (prevPrev == null) {
                continue; // Retry
            }
            
            if (right.prev.compareAndSet(prev, prevPrev)) {
                prevPrev.next.compareAndSet(prev, right);
                return prev.value;
            }
        }
    }
    
    public boolean isEmpty() {
        Node<T> left = leftSentinel.get();
        Node<T> right = rightSentinel.get();
        return left.next.get() == right;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Deque Demo ===\n");
        
        LockFreeDeque<Integer> deque = new LockFreeDeque<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 100 + j;
                    
                    // Push from left
                    deque.pushLeft(value);
                    System.out.println("Thread " + threadId + " pushed left: " + value);
                    
                    // Push from right
                    deque.pushRight(value + 1000);
                    System.out.println("Thread " + threadId + " pushed right: " + (value + 1000));
                    
                    // Pop from left
                    Integer left = deque.popLeft();
                    if (left != null) {
                        System.out.println("Thread " + threadId + " popped left: " + left);
                    }
                    
                    // Pop from right
                    Integer right = deque.popRight();
                    if (right != null) {
                        System.out.println("Thread " + threadId + " popped right: " + right);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nDeque operations completed!");
        System.out.println("Final deque empty: " + deque.isEmpty());
    }
}

