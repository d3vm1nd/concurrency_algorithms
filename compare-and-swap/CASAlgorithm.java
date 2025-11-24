import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CASAlgorithm {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int ITERATIONS_PER_THREAD = 3;
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private static final AtomicReference<String> sharedData = new AtomicReference<>("Initial");
    
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
        System.out.println("Final data value: " + sharedData.get());
    }

    private static void runThreadProcess(int threadId) {
        for (int i = 0; i < ITERATIONS_PER_THREAD; i++) {
            incrementCounterWithCAS(threadId);
            updateDataWithCAS(threadId);
            executeNonCriticalSection(threadId);
        }
    }

    private static void incrementCounterWithCAS(int threadId) {
        boolean success = false;
        while (!success) {
            int currentValue = sharedCounter.get();
            int newValue = currentValue + 1;
            success = sharedCounter.compareAndSet(currentValue, newValue);
            handleCASResult(success, threadId, currentValue, newValue);
        }
    }

    private static void handleCASResult(boolean success, int threadId, int oldValue, int newValue) {
        if (success) {
            System.out.println("Thread " + threadId + " updated counter: " + oldValue + " -> " + newValue);
        } else {
            Thread.onSpinWait();
        }
    }

    private static void updateDataWithCAS(int threadId) {
        boolean success = false;
        while (!success) {
            String currentData = sharedData.get();
            String newData = "Updated by Thread " + threadId;
            success = sharedData.compareAndSet(currentData, newData);
            if (!success) {
                Thread.onSpinWait();
            }
        }
    }
    
    private static void executeNonCriticalSection(int threadId) {
        System.out.println("Thread " + threadId + " in non-critical section");
    }
}

