import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hazard Pointers - Memory Reclamation for Lock-Free Data Structures
 * 
 * Hazard pointers are a memory reclamation technique that allows lock-free data
 * structures to safely reclaim memory that might still be accessed by other threads.
 * Each thread maintains a set of "hazard pointers" that protect nodes from being
 * reclaimed while they're being accessed.
 * 
 * This is a simplified demonstration of the hazard pointer pattern.
 */
public class HazardPointers<T> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int HAZARD_POINTER_COUNT = 2;
    
    // Per-thread hazard pointers
    @SuppressWarnings("unchecked")
    private final ThreadLocal<AtomicReference<Node<T>>[]> hazardPointers = 
        ThreadLocal.withInitial(() -> {
            AtomicReference<Node<T>>[] hp = new AtomicReference[HAZARD_POINTER_COUNT];
            for (int i = 0; i < HAZARD_POINTER_COUNT; i++) {
                hp[i] = new AtomicReference<>(null);
            }
            return hp;
        });
    
    // Global list of nodes pending reclamation
    private final AtomicReference<Node<T>> pendingReclamation = new AtomicReference<>(null);
    
    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicReference<>(null);
        }
    }
    
    /**
     * Set a hazard pointer to protect a node
     */
    public void setHazardPointer(int index, Node<T> node) {
        AtomicReference<Node<T>>[] hp = hazardPointers.get();
        if (index >= 0 && index < hp.length) {
            hp[index].set(node);
        }
    }
    
    /**
     * Clear a hazard pointer
     */
    public void clearHazardPointer(int index) {
        AtomicReference<Node<T>>[] hp = hazardPointers.get();
        if (index >= 0 && index < hp.length) {
            hp[index].set(null);
        }
    }
    
    /**
     * Check if a node is protected by any hazard pointer
     */
    public boolean isProtected(Node<T> node) {
        // Check all threads' hazard pointers
        // In a real implementation, this would check all threads
        AtomicReference<Node<T>>[] hp = hazardPointers.get();
        for (AtomicReference<Node<T>> ptr : hp) {
            if (ptr.get() == node) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Reclaim nodes that are not protected by hazard pointers
     */
    public void reclaim() {
        Node<T> current = pendingReclamation.get();
        Node<T> safe = null;
        
        while (current != null) {
            Node<T> next = current.next.get();
            
            if (!isProtected(current)) {
                // Safe to reclaim
                current.next.set(safe);
                safe = current;
            } else {
                // Still protected, keep in pending list
                current.next.set(safe);
                safe = current;
            }
            
            current = next;
        }
        
        // Update pending list with unreclaimed nodes
        pendingReclamation.set(safe);
    }
    
    /**
     * Add a node to pending reclamation
     */
    public void addToPendingReclamation(Node<T> node) {
        Node<T> current;
        do {
            current = pendingReclamation.get();
            node.next.set(current);
        } while (!pendingReclamation.compareAndSet(current, node));
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Hazard Pointers Demo ===\n");
        System.out.println("Demonstrating memory reclamation pattern for lock-free structures\n");
        
        HazardPointers<Integer> hp = new HazardPointers<>();
        
        // Create some nodes
        Node<Integer> node1 = new Node<>(1);
        Node<Integer> node2 = new Node<>(2);
        Node<Integer> node3 = new Node<>(3);
        
        // Simulate protecting a node
        System.out.println("Setting hazard pointer to protect node1");
        hp.setHazardPointer(0, node1);
        
        System.out.println("Node1 protected: " + hp.isProtected(node1));
        System.out.println("Node2 protected: " + hp.isProtected(node2));
        
        // Add unprotected node to reclamation
        System.out.println("\nAdding node2 to pending reclamation");
        hp.addToPendingReclamation(node2);
        
        // Clear hazard pointer
        System.out.println("Clearing hazard pointer");
        hp.clearHazardPointer(0);
        
        System.out.println("Node1 protected: " + hp.isProtected(node1));
        
        // Attempt reclamation
        System.out.println("\nAttempting reclamation");
        hp.reclaim();
        
        System.out.println("\nHazard pointers pattern demonstrated!");
        System.out.println("Note: This is a simplified demonstration.");
        System.out.println("Real implementations require checking all threads' hazard pointers.");
    }
}

