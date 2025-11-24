import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestAndSetLock {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int ITERATIONS_PER_THREAD = 3;
    private static final AtomicBoolean lock = new AtomicBoolean(false);
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    
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
            acquireLock();
            executeCriticalSection(threadId);
            releaseLock();
            executeNonCriticalSection(threadId);
        }
    }

    private static void acquireLock() {
        while (lock.getAndSet(true)) {
            Thread.onSpinWait();
        }
    }

    private static void releaseLock() {
        lock.set(false);
    }

    private static void executeCriticalSection(int threadId) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + threadId + " in critical section, counter: " + value);
    }
    
    private static void executeNonCriticalSection(int threadId) {
        System.out.println("Thread " + threadId + " in non-critical section");
    }
}

