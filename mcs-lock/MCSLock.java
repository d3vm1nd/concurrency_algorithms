import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MCSLock {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int ITERATIONS_PER_THREAD = 3;
    private static final MCSLock lock = new MCSLock();
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    
    private final AtomicReference<Node> tail = new AtomicReference<>(null);
    
    private static class Node {
        volatile boolean waiting = true;
        volatile Node next = null;
    }
    
    private final ThreadLocal<Node> myNode = ThreadLocal.withInitial(Node::new);
    
    public void acquire() {
        Node currentNode = myNode.get();
        Node previousNode = tail.getAndSet(currentNode);
        
        if (previousNode != null) {
            previousNode.next = currentNode;
            while (currentNode.waiting) {
                Thread.onSpinWait();
            }
        } else {
            currentNode.waiting = false;
        }
    }
    
    public void release() {
        Node currentNode = myNode.get();
        
        if (currentNode.next == null) {
            if (tail.compareAndSet(currentNode, null)) {
                return;
            }
            while (currentNode.next == null) {
                Thread.onSpinWait();
            }
        }
        
        currentNode.next.waiting = false;
        currentNode.next = null;
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
        
        System.out.println("Final counter value: " + sharedCounter.get());
    }
    
    private static void runThreadProcess(int threadId) {
        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
            lock.acquire();
            executeCriticalSection(threadId);
            lock.release();
            executeNonCriticalSection(threadId);
        }
    }
    
    private static void executeCriticalSection(int threadId) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + threadId + " in critical section, counter: " + value);
    }
    
    private static void executeNonCriticalSection(int threadId) {
        System.out.println("Thread " + threadId + " in non-critical section");
    }
}

