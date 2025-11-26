public class ABADemo {
    private static final int NUMBER_OF_THREADS = 3;
    private static final ABAProblem abaProblem = new ABAProblem();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ABA Problem Demonstration ===");
        System.out.println("Initial value: " + abaProblem.getCurrentValue());
        System.out.println();
        
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        threads[0] = Thread.ofVirtual().start(() -> runReaderThread(0));
        threads[1] = Thread.ofVirtual().start(() -> runABAThread(1));
        threads[2] = Thread.ofVirtual().start(() -> runReaderThread(2));
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println();
        System.out.println("Final value: " + abaProblem.getCurrentValue());
    }
    
    private static void runReaderThread(int threadId) {
        ABAProblem.ValueWrapper originalValue = abaProblem.getCurrentValue();
        System.out.println("Thread " + threadId + " reads: " + originalValue);
        
        simulateWork();
        
        boolean success = abaProblem.attemptUpdate(originalValue, 
            new ABAProblem.ValueWrapper("C", originalValue.version + 10));
        
        if (success) {
            System.out.println("Thread " + threadId + " CAS succeeded!");
        } else {
            System.out.println("Thread " + threadId + " CAS failed!");
        }
    }
    
    private static void runABAThread(int threadId) {
        simulateWork();
        System.out.println("Thread " + threadId + " performs A->B->A change");
        abaProblem.performABAChange();
    }
    
    private static void simulateWork() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

