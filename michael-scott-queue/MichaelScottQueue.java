import java.util.concurrent.atomic.AtomicReference;

public class MichaelScottQueue<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 4;
    
    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;
    
    private static class Node<T> {
        final T value;
        volatile AtomicReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }
    
    public MichaelScottQueue() {
        Node<T> dummyNode = new Node<>(null);
        this.head = new AtomicReference<>(dummyNode);
        this.tail = new AtomicReference<>(dummyNode);
    }
    
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);
        
        while (true) {
            Node<T> currentTail = tail.get();
            Node<T> tailNext = currentTail.next.get();
            
            if (currentTail != tail.get()) {
                continue;
            }
            
            if (tailNext == null) {
                if (currentTail.next.compareAndSet(null, newNode)) {
                    tail.compareAndSet(currentTail, newNode);
                    return;
                }
            } else {
                tail.compareAndSet(currentTail, tailNext);
            }
        }
    }
    
    public T dequeue() {
        while (true) {
            Node<T> currentHead = head.get();
            Node<T> currentTail = tail.get();
            Node<T> headNext = currentHead.next.get();
            
            if (currentHead != head.get()) {
                continue;
            }
            
            if (currentHead == currentTail) {
                if (headNext == null) {
                    return null;
                }
                tail.compareAndSet(currentTail, headNext);
            } else {
                if (head.compareAndSet(currentHead, headNext)) {
                    return headNext.value;
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return head.get() == tail.get() && 
               head.get().next.get() == null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        MichaelScottQueue<Integer> queue = new MichaelScottQueue<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> 
                processQueueOperations(queue, threadId));
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nQueue operations completed!");
        System.out.println("Final queue empty: " + queue.isEmpty());
    }
    
    private static void processQueueOperations(
            MichaelScottQueue<Integer> queue, int threadId) {
        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
            int value = threadId * OPERATIONS_PER_THREAD + j;
            queue.enqueue(value);
            System.out.println("Thread " + threadId + " enqueued: " + value);
            try { Thread.sleep(10); } 
            catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
            }
            Integer dequeued = queue.dequeue();
            if (dequeued != null) {
                System.out.println("Thread " + threadId + 
                    " dequeued: " + dequeued);
            }
        }
    }
}

