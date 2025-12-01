import java.util.concurrent.atomic.AtomicReference;

/**
 * Double-Checked Locking Pattern
 * 
 * A design pattern used to reduce the overhead of acquiring a lock by first
 * testing the locking criterion without actually acquiring the lock. Only if
 * the check indicates that locking is required does the actual locking proceed.
 * 
 * This pattern is particularly useful for lazy initialization of singletons
 * and other expensive-to-create objects in multithreaded environments.
 * 
 * Note: In modern Java, using 'volatile' keyword or AtomicReference ensures
 * proper visibility and prevents the need for explicit synchronization in
 * many cases.
 */
public class DoubleCheckedLocking {
    private static final int NUMBER_OF_THREADS = 10;
    
    /**
     * Example 1: Singleton with Double-Checked Locking (Traditional)
     * Uses synchronized block with double-check
     */
    public static class SingletonTraditional {
        private static SingletonTraditional instance;
        private final String data;
        
        private SingletonTraditional() {
            // Simulate expensive initialization
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.data = "Initialized at " + System.currentTimeMillis();
        }
        
        public static SingletonTraditional getInstance() {
            // First check (without locking)
            if (instance == null) {
                synchronized (SingletonTraditional.class) {
                    // Second check (with locking)
                    if (instance == null) {
                        instance = new SingletonTraditional();
                    }
                }
            }
            return instance;
        }
        
        public String getData() {
            return data;
        }
    }
    
    /**
     * Example 2: Singleton with volatile (Modern Java approach)
     * Uses volatile keyword for proper visibility
     */
    public static class SingletonVolatile {
        private static volatile SingletonVolatile instance;
        private final String data;
        
        private SingletonVolatile() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.data = "Initialized at " + System.currentTimeMillis();
        }
        
        public static SingletonVolatile getInstance() {
            if (instance == null) {
                synchronized (SingletonVolatile.class) {
                    if (instance == null) {
                        instance = new SingletonVolatile();
                    }
                }
            }
            return instance;
        }
        
        public String getData() {
            return data;
        }
    }
    
    /**
     * Example 3: Singleton with AtomicReference (Lock-free approach)
     * Uses atomic operations instead of synchronization
     */
    public static class SingletonAtomic {
        private static final AtomicReference<SingletonAtomic> instance = 
            new AtomicReference<>(null);
        private final String data;
        
        private SingletonAtomic() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.data = "Initialized at " + System.currentTimeMillis();
        }
        
        public static SingletonAtomic getInstance() {
            SingletonAtomic current = instance.get();
            if (current == null) {
                SingletonAtomic newInstance = new SingletonAtomic();
                if (instance.compareAndSet(null, newInstance)) {
                    return newInstance;
                } else {
                    return instance.get();
                }
            }
            return current;
        }
        
        public String getData() {
            return data;
        }
    }
    
    /**
     * Example 4: Lazy Initialization with Double-Checked Locking
     */
    public static class ExpensiveResource {
        private volatile Resource resource;
        
        public Resource getResource() {
            Resource result = resource;
            if (result == null) {
                synchronized (this) {
                    result = resource;
                    if (result == null) {
                        resource = result = createResource();
                    }
                }
            }
            return result;
        }
        
        private Resource createResource() {
            System.out.println("Creating expensive resource...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new Resource("Resource created at " + System.currentTimeMillis());
        }
    }
    
    private static class Resource {
        private final String data;
        
        public Resource(String data) {
            this.data = data;
        }
        
        public String getData() {
            return data;
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Double-Checked Locking Pattern Demo ===\n");
        
        // Test Traditional Singleton
        testSingleton("Traditional", () -> SingletonTraditional.getInstance());
        
        // Test Volatile Singleton
        testSingleton("Volatile", () -> SingletonVolatile.getInstance());
        
        // Test Atomic Singleton
        testSingleton("Atomic", () -> SingletonAtomic.getInstance());
        
        // Test Lazy Initialization
        testLazyInitialization();
    }
    
    private static void testSingleton(String name, 
                                     java.util.function.Supplier<?> supplier) 
            throws InterruptedException {
        System.out.println("Testing " + name + " Singleton:");
        
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        java.util.Set<Object> instances = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads[i] = Thread.ofVirtual().start(() -> {
                Object instance = supplier.get();
                instances.add(instance);
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("  Number of unique instances: " + instances.size());
        System.out.println("  Correctness: " + 
            (instances.size() == 1 ? "✓ PASSED (only one instance)" : "✗ FAILED"));
        System.out.println();
    }
    
    private static void testLazyInitialization() throws InterruptedException {
        System.out.println("Testing Lazy Initialization:");
        
        ExpensiveResource resource = new ExpensiveResource();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        java.util.Set<String> creationTimes = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>());
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads[i] = Thread.ofVirtual().start(() -> {
                Resource res = resource.getResource();
                creationTimes.add(res.getData());
            });
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("  Number of resource creations: " + creationTimes.size());
        System.out.println("  Correctness: " + 
            (creationTimes.size() == 1 ? "✓ PASSED (created only once)" : "✗ FAILED"));
    }
}

