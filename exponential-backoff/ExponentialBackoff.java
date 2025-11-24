import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ExponentialBackoff {
    private static final Semaphore resource = new Semaphore(1);
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private static final int MAX_RETRIES = 5;
    private static final int INITIAL_DELAY_MS = 10;
    
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = Thread.ofVirtual().start(() -> accessResourceWithBackoff(1));
        Thread thread2 = Thread.ofVirtual().start(() -> accessResourceWithBackoff(2));
        Thread thread3 = Thread.ofVirtual().start(() -> accessResourceWithBackoff(3));
        
        thread1.join();
        thread2.join();
        thread3.join();
        
        System.out.println("Final counter value: " + sharedCounter.get());
    }
    
    private static void accessResourceWithBackoff(int threadId) {
        for (int attempt = 0; attempt < 3; attempt++) {
            if (tryAccessWithExponentialBackoff(threadId, attempt)) {
                performWork(threadId);
                releaseResource(threadId);
                return;
            }
        }
        System.out.println("Thread " + threadId + " failed after all retries");
    }
    
    private static boolean tryAccessWithExponentialBackoff(int threadId, int attempt) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            if (resource.tryAcquire()) {
                return true;
            }
            waitWithExponentialBackoff(retryCount, threadId);
            retryCount++;
        }
        return false;
    }
    
    private static void waitWithExponentialBackoff(int retryCount, int threadId) {
        long delayMs = INITIAL_DELAY_MS * (1L << retryCount);
        System.out.println("Thread " + threadId + " waiting " + delayMs + "ms (retry " + (retryCount + 1) + ")");
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void performWork(int threadId) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + threadId + " acquired resource, counter: " + value);
    }
    
    private static void releaseResource(int threadId) {
        resource.release();
        System.out.println("Thread " + threadId + " released resource");
    }
}

