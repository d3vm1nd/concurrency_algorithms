import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TreiberStack<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 4;
    private static final TreiberStack<Integer> stack = new TreiberStack<>();
    private static final AtomicInteger operationCounter = new AtomicInteger(0);
    
    private final AtomicReference<Node<T>> head = new AtomicReference<>(null);
    
    private static class Node<T> {
        final T value;
        Node<T> next;
        
        Node(T value) {
            this.value = value;
        }
    }
    
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        Node<T> currentHead;
        
        do {
            currentHead = head.get();
            newNode.next = currentHead;
        } while (!head.compareAndSet(currentHead, newNode));
    }
    
    public T pop() {
        Node<T> currentHead;
        Node<T> nextNode;
        
        do {
            currentHead = head.get();
            if (currentHead == null) return null;
            nextNode = currentHead.next;
        } while (!head.compareAndSet(currentHead, nextNode));
        
        return currentHead.value;
    }
    
    public boolean isEmpty() {
        return head.get() == null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> runThreadProcess(threadId));
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Total operations completed: " + operationCounter.get());
        System.out.println("Stack is empty: " + stack.isEmpty());
    }
    
    private static void runThreadProcess(int threadId) {
        for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
            performPushOperation(threadId, i);
            performPopOperation(threadId);
        }
    }
    
    private static void performPushOperation(int threadId, int index) {
        int value = threadId * 10 + index;
        stack.push(value);
        operationCounter.incrementAndGet();
        System.out.println("Thread " + threadId + " pushed: " + value);
    }
    
    private static void performPopOperation(int threadId) {
        Integer popped = stack.pop();
        if (popped != null) {
            operationCounter.incrementAndGet();
            System.out.println("Thread " + threadId + " popped: " + popped);
        }
    }
}

