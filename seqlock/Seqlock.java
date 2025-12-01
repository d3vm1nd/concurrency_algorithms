import java.util.concurrent.atomic.AtomicInteger;

/**
 * Seqlock (Sequence Lock)
 * 
 * A synchronization mechanism that allows multiple readers to proceed concurrently
 * without blocking, while writers use a sequence number to indicate updates.
 * Readers check the sequence number before and after reading to detect concurrent
 * writes and retry if necessary.
 * 
 * Key features:
 * - Optimistic reading (no locks for readers)
 * - Writers are exclusive
 * - Readers never block writers
 * - Suitable for read-heavy workloads
 */
public class Seqlock {
    private static final int NUMBER_OF_READERS = 10;
    private static final int NUMBER_OF_WRITERS = 2;
    private static final int READ_ITERATIONS = 100;
    private static final int WRITE_ITERATIONS = 20;
    
    private final AtomicInteger sequence = new AtomicInteger(0);
    private volatile int data = 0;
    
    /**
     * Read operation - optimistic, lock-free for readers
     */
    public int read() {
        int seq;
        int value = 0;
        
        do {
            seq = sequence.get();
            if ((seq & 1) != 0) {
                // Odd sequence means writer is active, retry
                Thread.onSpinWait();
                continue;
            }
            value = data; // Read the data
        } while (sequence.get() != seq || (seq & 1) != 0);
        
        return value;
    }
    
    /**
     * Write operation - exclusive, uses sequence number
     */
    public void write(int newValue) {
        // Increment sequence (make it odd to indicate write in progress)
        int seq = sequence.incrementAndGet();
        
        // Perform the write
        data = newValue;
        
        // Increment sequence again (make it even to indicate write complete)
        sequence.incrementAndGet();
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Seqlock Demo ===\n");
        
        Seqlock seqlock = new Seqlock();
        AtomicInteger readCount = new AtomicInteger(0);
        AtomicInteger writeCount = new AtomicInteger(0);
        
        // Create reader threads
        Thread[] readers = new Thread[NUMBER_OF_READERS];
        for (int i = 0; i < NUMBER_OF_READERS; i++) {
            final int readerId = i;
            readers[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < READ_ITERATIONS; j++) {
                    int value = seqlock.read();
                    readCount.incrementAndGet();
                    
                    if (j % 20 == 0) {
                        System.out.println("Reader " + readerId + " read: " + value);
                    }
                    
                    // Simulate some processing
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Create writer threads
        Thread[] writers = new Thread[NUMBER_OF_WRITERS];
        for (int i = 0; i < NUMBER_OF_WRITERS; i++) {
            final int writerId = i;
            writers[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < WRITE_ITERATIONS; j++) {
                    int newValue = writerId * 1000 + j;
                    seqlock.write(newValue);
                    writeCount.incrementAndGet();
                    System.out.println("Writer " + writerId + " wrote: " + newValue);
                    
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // Wait for all threads
        for (Thread reader : readers) {
            reader.join();
        }
        for (Thread writer : writers) {
            writer.join();
        }
        
        System.out.println("\n=== Results ===");
        System.out.println("Total reads: " + readCount.get());
        System.out.println("Total writes: " + writeCount.get());
        System.out.println("Final data value: " + seqlock.read());
    }
}

