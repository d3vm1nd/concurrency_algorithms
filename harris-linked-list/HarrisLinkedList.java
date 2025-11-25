import java.util.concurrent.atomic.AtomicMarkableReference;

public class HarrisLinkedList<T extends Comparable<T>> {
    private static final int NUMBER_OF_THREADS = 5;
    private static final int OPERATIONS_PER_THREAD = 4;
    
    private final Node<T> head;
    
    private static class Node<T> {
        final T value;
        final AtomicMarkableReference<Node<T>> next;
        
        Node(T value) {
            this.value = value;
            this.next = new AtomicMarkableReference<>(null, false);
        }
    }
    
    private static class Window<T> {
        final Node<T> predecessor;
        final Node<T> current;
        
        Window(Node<T> predecessor, Node<T> current) {
            this.predecessor = predecessor;
            this.current = current;
        }
    }
    
    public HarrisLinkedList() {
        this.head = new Node<>(null);
    }
    
    public boolean contains(T value) {
        Node<T> node = locateNode(value);
        return node != null && 
               node.value != null && 
               node.value.equals(value) &&
               !isMarked(node);
    }
    
    public boolean add(T value) {
        Node<T> newNode = new Node<>(value);
        
        while (true) {
            Window<T> window = locateInsertionWindow(value);
            if (hasValueAtNode(window.current, value)) {
                return false;
            }
            if (attemptInsertion(window.predecessor, window.current, newNode)) {
                return true;
            }
        }
    }
    
    private boolean hasValueAtNode(Node<T> node, T value) {
        return node != null && node.value.equals(value);
    }
    
    private boolean attemptInsertion(Node<T> predecessor, 
                                    Node<T> current, Node<T> newNode) {
        newNode.next.set(current, false);
        return predecessor.next.compareAndSet(current, newNode, false, false);
    }
    
    public boolean remove(T value) {
        while (true) {
            Window<T> window = locateInsertionWindow(value);
            if (!hasValueAtNode(window.current, value)) {
                return false;
            }
            if (attemptMarkAndUnlink(window.predecessor, window.current)) {
                return true;
            }
        }
    }
    
    private boolean attemptMarkAndUnlink(Node<T> predecessor, Node<T> current) {
        Node<T> successor = getNext(current);
        if (!current.next.compareAndSet(successor, successor, false, true)) {
            return false;
        }
        predecessor.next.compareAndSet(current, successor, false, false);
        return true;
    }
    
    private Window<T> locateInsertionWindow(T targetValue) {
        Node<T> predecessor = head;
        Node<T> current = getNext(predecessor);
        
        while (current != null) {
            Node<T> successor = getNext(current);
            if (isMarked(current)) {
                removeMarkedNode(predecessor, current, successor);
                current = successor;
            } else if (hasReachedTargetPosition(current, targetValue)) {
                return new Window<>(predecessor, current);
            } else {
                predecessor = current;
                current = successor;
            }
        }
        
        return new Window<>(predecessor, null);
    }
    
    private void removeMarkedNode(Node<T> predecessor, 
                                  Node<T> current, Node<T> successor) {
        predecessor.next.compareAndSet(current, successor, false, false);
    }
    
    private boolean hasReachedTargetPosition(Node<T> current, T targetValue) {
        return current.value != null && 
               current.value.compareTo(targetValue) >= 0;
    }
    
    private Node<T> locateNode(T targetValue) {
        Node<T> current = getNext(head);
        
        while (current != null) {
            if (matchesValue(current, targetValue)) {
                return current;
            }
            current = getNext(current);
        }
        
        return null;
    }
    
    private boolean matchesValue(Node<T> node, T targetValue) {
        return !isMarked(node) && 
               node.value != null && 
               node.value.equals(targetValue);
    }
    
    private Node<T> getNext(Node<T> node) {
        return node.next.getReference();
    }
    
    private boolean isMarked(Node<T> node) {
        return node.next.isMarked();
    }
    
    public static void main(String[] args) throws InterruptedException {
        HarrisLinkedList<Integer> list = new HarrisLinkedList<>();
        Thread[] threads = new Thread[NUMBER_OF_THREADS];
        
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            final int threadId = i;
            threads[i] = Thread.ofVirtual().start(() -> 
                processOperations(list, threadId));
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("\nList operations completed!");
    }
    
    private static void processOperations(
            HarrisLinkedList<Integer> list, int threadId) {
        for (int operationIndex = 0; operationIndex < OPERATIONS_PER_THREAD; operationIndex++) {
            int value = threadId * OPERATIONS_PER_THREAD + operationIndex;
            list.add(value);
            System.out.println("Thread " + threadId + " added: " + value);
            
            boolean found = list.contains(value);
            System.out.println("Thread " + threadId + " found " + value + ": " + found);
            
            if (operationIndex % 2 == 0) {
                boolean removed = list.remove(value);
                System.out.println("Thread " + threadId + " removed " + value + ": " + removed);
            }
        }
    }
}

