import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CLHLock {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int ITERATIONS_PER_THREAD = 3;
    private static final CLHLock lock = new CLHLock();
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    
    private final AtomicReference<Node> tail = new AtomicReference<>(new Node());
    
    private static class Node {
        volatile boolean locked = false;
    }
    
    private final ThreadLocal<Node> currentNode = ThreadLocal.withInitial(Node::new);
    private final ThreadLocal<Node> predecessorNode = new ThreadLocal<>();
    
    public void acquire() {
        Node myNode = currentNode.get();
        myNode.locked = true;
        
        Node previousNode = tail.getAndSet(myNode);
        predecessorNode.set(previousNode);
        
        spinUntilUnlocked(previousNode);
    }
    
    private void spinUntilUnlocked(Node node) {
        while (node.locked) {
            Thread.onSpinWait();
        }
    }
    
    public void release() {
        Node myNode = currentNode.get();
        myNode.locked = false;
        currentNode.set(predecessorNode.get());
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

