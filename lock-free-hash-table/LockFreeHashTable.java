import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeHashTable<K, V> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 4;
    private static final int INITIAL_CAPACITY = 16;
    
    private final AtomicReferenceArray<AtomicReference<BucketNode<K, V>>> buckets;
    
    private static class BucketNode<K, V> {
        final K key;
        volatile V value;
        final AtomicReference<BucketNode<K, V>> next;
        
        BucketNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }
    
    public LockFreeHashTable() {
        this.buckets = new AtomicReferenceArray<>(INITIAL_CAPACITY);
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            buckets.set(i, new AtomicReference<>(null));
        }
    }
    
    public void put(K key, V value) {
        int bucketIndex = Math.abs(key.hashCode()) % buckets.length();
        AtomicReference<BucketNode<K, V>> bucketRef = buckets.get(bucketIndex);
        while (true) {
            BucketNode<K, V> head = bucketRef.get();
            BucketNode<K, V> existing = findNode(head, key);
            if (existing != null) {
                existing.value = value;
                return;
            }
            BucketNode<K, V> newNode = new BucketNode<>(key, value);
            newNode.next.set(head);
            if (bucketRef.compareAndSet(head, newNode)) {
                return;
            }
        }
    }
    
    public V get(K key) {
        int bucketIndex = Math.abs(key.hashCode()) % buckets.length();
        BucketNode<K, V> head = buckets.get(bucketIndex).get();
        BucketNode<K, V> found = findNode(head, key);
        return found != null ? found.value : null;
    }
    
    public boolean remove(K key) {
        int bucketIndex = Math.abs(key.hashCode()) % buckets.length();
        AtomicReference<BucketNode<K, V>> bucketRef = buckets.get(bucketIndex);
        while (true) {
            BucketNode<K, V> head = bucketRef.get();
            if (head == null) {
                return false;
            }
            if (head.key != null && head.key.equals(key)) {
                if (bucketRef.compareAndSet(head, head.next.get())) {
                    return true;
                }
            } else if (removeFromList(head, key)) {
                return true;
            }
        }
    }
    
    private boolean removeFromList(BucketNode<K, V> head, K key) {
        BucketNode<K, V> prev = head;
        BucketNode<K, V> curr = head.next.get();
        while (curr != null) {
            if (curr.key != null && curr.key.equals(key)) {
                if (prev.next.compareAndSet(curr, curr.next.get())) {
                    return true;
                }
                return false;
            }
            prev = curr;
            curr = curr.next.get();
        }
        return false;
    }
    
    private BucketNode<K, V> findNode(BucketNode<K, V> head, K key) {
        BucketNode<K, V> curr = head;
        while (curr != null) {
            if (curr.key != null && curr.key.equals(key)) {
                return curr;
            }
            curr = curr.next.get();
        }
        return null;
    }
    
    public static void main(String[] args) throws InterruptedException {
        LockFreeHashTable<Integer, String> table = new LockFreeHashTable<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    int key = threadId * OPERATIONS_PER_THREAD + j;
                    table.put(key, "Value-" + key);
                    System.out.println("Thread " + threadId + " put: " + key);
                    String retrieved = table.get(key);
                    System.out.println("Thread " + threadId + " got: " + key + " -> " + retrieved);
                    if (j % 2 == 0) {
                        boolean removed = table.remove(key);
                        System.out.println("Thread " + threadId + " removed: " + key + " -> " + removed);
                    }
                }
            });
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("\nHash table operations completed!");
    }
}
