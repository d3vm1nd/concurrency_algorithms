import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class BakeryAlgorithm {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int ITERATIONS_PER_THREAD = 3;
    private static final AtomicBoolean[] isChoosingTicket = new AtomicBoolean[NUMBER_OF_THREADS];
    private static final AtomicIntegerArray ticketNumber = new AtomicIntegerArray(NUMBER_OF_THREADS);
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    
    static {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            isChoosingTicket[i] = new AtomicBoolean(false);
        }
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
            enterCriticalSection(threadId);
            executeCriticalSection(threadId);
            exitCriticalSection(threadId);
            executeNonCriticalSection(threadId);
        }
    }

    private static void enterCriticalSection(int threadId) {
        isChoosingTicket[threadId].set(true);
        int maxTicket = findMaximumTicketNumber();
        ticketNumber.set(threadId, maxTicket + 1);
        isChoosingTicket[threadId].set(false);
        waitForAllThreadsToFinishChoosing();
        waitForTurn(threadId);
    }

    private static int findMaximumTicketNumber() {
        int maximumTicket = 0;
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            int currentTicket = ticketNumber.get(i);
            if (currentTicket > maximumTicket) {
                maximumTicket = currentTicket;
            }
        }
        return maximumTicket;
    }

    private static void waitForAllThreadsToFinishChoosing() {
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            while (isChoosingTicket[i].get()) {
                Thread.onSpinWait();
            }
        }
    }

    private static void waitForTurn(int threadId) {
        int myTicket = ticketNumber.get(threadId);
        for (int otherId = 0; otherId < NUMBER_OF_THREADS; otherId++) {
            if (otherId != threadId) {
                while (hasHigherPriority(otherId, threadId, myTicket)) {
                    Thread.onSpinWait();
                }
            }
        }
    }

    private static boolean hasHigherPriority(int otherId, int myId, int myTicket) {
        int otherTicket = ticketNumber.get(otherId);
        if (otherTicket == 0) {
            return false;
        }
        return (otherTicket < myTicket) || (otherTicket == myTicket && otherId < myId);
    }

    private static void exitCriticalSection(int threadId) {
        ticketNumber.set(threadId, 0);
    }

    private static void executeCriticalSection(int threadId) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + threadId + " in critical section, counter: " + value);
    }
    
    private static void executeNonCriticalSection(int threadId) {
        System.out.println("Thread " + threadId + " in non-critical section");
    }
}

