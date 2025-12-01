import java.util.concurrent.atomic.AtomicReference;
import java.util.Random;

/**
 * Elimination Backoff Stack
 * 
 * An optimized lock-free stack that uses an "elimination array" to allow
 * concurrent push and pop operations to cancel each other out without
 * accessing the main stack. This reduces contention and improves scalability.
 * 
 * Key features:
 * - Lock-free operations
 * - Elimination array for contention reduction
 * - Exponential backoff
 * - High scalability
 */
public class EliminationBackoffStack<T> {
    private static final int NUMBER_OF_THREADS = 10;
    private static final int OPERATIONS_PER_THREAD = 20;
    private static final int ELIMINATION_ARRAY_SIZE = 16;
    private static final int MAX_BACKOFF = 100;
    
    private final AtomicReference<Node<T>> top = new AtomicReference<>(null);
    private final EliminationArray<T> eliminationArray;
    
    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }
    
    private static class EliminationArray<T> {
        private final AtomicReference<Slot<T>>[] slots;
        private final Random random = new Random();
        
        @SuppressWarnings("unchecked")
        EliminationArray(int size) {
            this.slots = new AtomicReference[size];
            for (int i = 0; i < size; i++) {
                this.slots[i] = new AtomicReference<>(new Slot<>(null, false));
            }
        }
        
        T visit(T value, int timeout) throws TimeoutException {
            int slotIndex = random.nextInt(slots.length);
            Slot<T> slot = slots[slotIndex].get();
            
            if (slot.value == null && slot.isWaiting) {
                // Try to match with waiting operation
                Slot<T> newSlot = new Slot<>(value, false);
                if (slots[slotIndex].compareAndSet(slot, newSlot)) {
                    // Matched! Return the waiting value
                    return slot.value;
                }
            }
            
            // No match, wait for match
            long startTime = System.nanoTime();
            Slot<T> waitSlot = new Slot<>(value, true);
            slots[slotIndex].set(waitSlot);
            
            while (System.nanoTime() - startTime < timeout) {
                Slot<T> current = slots[slotIndex].get();
                if (!current.isWaiting && current.value != null) {
                    // Matched!
                    slots[slotIndex].set(new Slot<>(null, false));
                    return current.value;
                }
                Thread.onSpinWait();
            }
            
            // Timeout
            slots[slotIndex].compareAndSet(waitSlot, new Slot<>(null, false));
            throw new TimeoutException();
        }
    }
    
    private static class Slot<T> {
        final T value;
        final boolean isWaiting;
        
        Slot(T value, boolean isWaiting) {
            this.value = value;
            this.isWaiting = isWaiting;
        }
    }
    
    private static class TimeoutException extends Exception {
    }
    
    public EliminationBackoffStack() {
        this.eliminationArray = new EliminationArray<>(ELIMINATION_ARRAY_SIZE);
    }
    
    /**
     * Push an element onto the stack
     */
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        int backoff = 1;
        
        while (true) {
            // Try direct push to stack
            Node<T> currentTop = top.get();
            newNode.next.set(currentTop);
            
            if (top.compareAndSet(currentTop, newNode)) {
                return; // Success
            }
            
            // Direct push failed, try elimination
            try {
                eliminationArray.visit(item, backoff * 1000); // Timeout in nanoseconds
                return; // Matched with a pop
            } catch (TimeoutException e) {
                // Elimination failed, backoff and retry
                backoff = Math.min(backoff * 2, MAX_BACKOFF);
                try {
                    Thread.sleep(0, backoff); // Nanosecond sleep
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Pop an element from the stack
     */
    public T pop() {
        int backoff = 1;
        
        while (true) {
            // Try direct pop from stack
            Node<T> currentTop = top.get();
            if (currentTop == null) {
                // Stack empty, try elimination
                try {
                    T value = eliminationArray.visit(null, backoff * 1000);
                    return value; // Matched with a push
                } catch (TimeoutException e) {
                    return null; // No match, stack empty
                }
            }
            
            Node<T> next = currentTop.next.get();
            if (top.compareAndSet(currentTop, next)) {
                return currentTop.value; // Success
            }
            
            // Direct pop failed, try elimination
            try {
                T value = eliminationArray.visit(null, backoff * 1000);
                return value; // Matched with a push
            } catch (TimeoutException e) {
                // Elimination failed, backoff and retry
                backoff = Math.min(backoff * 2, MAX_BACKOFF);
                try {
                    Thread.sleep(0, backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return top.get() == null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Elimination Backoff Stack Demo ===\n");
        
        EliminationBackoffStack<Integer> stack = new EliminationBackoffStack<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int value = threadId * 100 + j;
                    
                    // Push
                    stack.push(value);
                    System.out.println("Thread " + threadId + " pushed: " + value);
                    
                    // Pop
                    Integer popped = stack.pop();
                    if (popped != null) {
                        System.out.println("Thread " + threadId + " popped: " + popped);
                    }
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nStack operations completed!");
        System.out.println("Final stack empty: " + stack.isEmpty());
    }
}

