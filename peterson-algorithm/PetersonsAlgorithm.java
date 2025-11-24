import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PetersonsAlgorithm {
    private static final AtomicBoolean flag0 = new AtomicBoolean(false);
    private static final AtomicBoolean flag1 = new AtomicBoolean(false);
    private static final AtomicInteger turn = new AtomicInteger(0);
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException {
        Thread thread0 = Thread.ofVirtual().start(() -> process(0));
        Thread thread1 = Thread.ofVirtual().start(() -> process(1));
        thread0.join();
        thread1.join();
        System.out.println("Final counter value: " + sharedCounter.get());
    }
    
    private static void process(int id) {
        for (int i = 0; i < 5; i++) {
            enterCriticalSection(id);
            criticalSection(id);
            exitCriticalSection(id);
            nonCriticalSection(id);
        }
    }
    
    private static void enterCriticalSection(int id) {
        int other = 1 - id;
        if (id == 0) flag0.set(true); else flag1.set(true);
        turn.set(other);
        AtomicBoolean otherFlag = (other == 0) ? flag0 : flag1;
        while (otherFlag.get() && turn.get() == other) {
            Thread.onSpinWait();
        }
    }
    
    private static void exitCriticalSection(int id) {
        if (id == 0) flag0.set(false); else flag1.set(false);
    }
    
    private static void criticalSection(int id) {
        int value = sharedCounter.incrementAndGet();
        System.out.println("Thread " + id + " in critical section, counter: " + value);
    }
    
    private static void nonCriticalSection(int id) {
        System.out.println("Thread " + id + " in non-critical section");
    }
}

