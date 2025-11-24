# Treiber Stack Algorithm

## What is Treiber Stack?

Treiber Stack is a **lock-free stack** data structure that provides thread-safe push and pop operations without using traditional locks. Named after R. Kent Treiber who first described it in 1986, this algorithm is one of the simplest and most elegant lock-free data structures, making it a fundamental building block in concurrent programming.

## The Problem It Solves

Traditional stack implementations face several challenges in concurrent environments:

- **Lock Contention**: Mutex-based stacks create bottlenecks when multiple threads compete for the lock
- **Deadlocks**: Lock-based implementations can lead to deadlocks if not carefully designed
- **Performance**: Lock overhead reduces throughput, especially under high contention
- **Scalability**: Lock-based solutions don't scale well with increasing thread counts

Treiber Stack solves these issues by:

- **Lock-Free Operations**: Uses atomic compare-and-swap (CAS) operations instead of locks
- **Wait-Free Progress**: Threads never block waiting for locks
- **High Concurrency**: Multiple threads can operate simultaneously without blocking
- **Simplicity**: Elegant and easy to understand algorithm

## How It Works

Treiber Stack maintains a **linked list of nodes** with an atomic reference to the head:

### The Stack Structure

The stack consists of:
- `head`: An atomic reference to the top node of the stack (null if empty)
- `Node<T>`: Each node contains a value and a reference to the next node

### The Algorithm Steps

#### Pushing an Item

1. **Create Node**: Create a new node with the item to push
2. **Read Head**: Read the current head of the stack
3. **Link Node**: Set the new node's next pointer to the current head
4. **CAS Update**: Use compare-and-swap to atomically update head to the new node
5. **Retry if Needed**: If CAS fails (another thread modified head), retry from step 2

The CAS operation ensures that the head is only updated if it hasn't changed since we read it, preventing lost updates.

#### Popping an Item

1. **Read Head**: Read the current head of the stack
2. **Check Empty**: If head is null, return null (stack is empty)
3. **Read Next**: Read the next node from the current head
4. **CAS Update**: Use compare-and-swap to atomically update head to the next node
5. **Retry if Needed**: If CAS fails (another thread modified head), retry from step 1
6. **Return Value**: Return the value from the popped node

The CAS loop ensures that we only pop if the head hasn't changed, maintaining stack consistency.

## Running the Program

```bash
cd treiber-stack
javac TreiberStack.java
java TreiberStack
```

## What You'll See

The program creates five virtual threads that each perform multiple push and pop operations:

- Each thread pushes values onto the stack
- Each thread pops values from the stack
- All operations are lock-free and thread-safe
- The output shows the concurrent operations and final state

## Example Output

```
Thread 0 pushed: 0
Thread 1 pushed: 10
Thread 2 pushed: 20
Thread 0 popped: 20
Thread 3 pushed: 30
Thread 1 popped: 30
Thread 4 pushed: 40
Thread 2 popped: 40
...
Total operations completed: 40
Stack is empty: true
```

## Key Features

✅ **Lock-Free**: No locks or blocking operations  
✅ **Thread-Safe**: Multiple threads can push and pop simultaneously  
✅ **Wait-Free Progress**: Threads never block waiting for locks  
✅ **Simple Design**: Elegant algorithm with minimal complexity  
✅ **High Performance**: Excellent scalability under contention  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicReference (`java.util.concurrent.atomic.AtomicReference`)

- Thread-safe reference to the head of the stack
- `get()`: Atomically reads the current head value
- `compareAndSet(expected, update)`: Atomically updates head only if it matches expected value
- Ensures atomic stack operations without explicit locking

### Compare-and-Swap (CAS) Loop

The CAS loop pattern is fundamental to lock-free programming:

```java
do {
    currentHead = head.get();
    newNode.next = currentHead;
} while (!head.compareAndSet(currentHead, newNode));
```

This loop:
1. Reads the current state
2. Prepares the new state
3. Attempts to atomically update
4. Retries if another thread modified the state

### Virtual Threads (`Thread.ofVirtual()`)

- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for high-concurrency scenarios and testing lock-free algorithms

### Node Structure

Each node contains:
- `value`: The data stored in the stack
- `next`: Reference to the next node (null for the last node)

## Algorithm Complexity

- **Time Complexity**: 
  - Push: O(1) average case, O(k) worst case where k is contention level
  - Pop: O(1) average case, O(k) worst case where k is contention level
- **Space Complexity**: O(n) where n is the number of items in the stack
- **Contention**: CAS retries increase with contention, but no blocking occurs

## ABA Problem

Treiber Stack is susceptible to the **ABA problem** in theory:
- Thread A reads head = X
- Thread B pops X, pushes Y, then pushes X again (same address)
- Thread A's CAS succeeds even though the stack changed

However, in practice:
- Modern garbage-collected languages (like Java) mitigate this issue
- The node addresses are typically not reused immediately
- For production use, consider using versioned pointers or hazard pointers

## Comparison with Lock-Based Stack

| Feature | Treiber Stack | Lock-Based Stack |
|---------|---------------|------------------|
| Lock-Free | ✅ Yes | ❌ No |
| Blocking | ❌ No | ✅ Yes |
| Deadlock Risk | ✅ None | ⚠️ Possible |
| Scalability | ✅ Excellent | ⚠️ Moderate |
| Complexity | ✅ Simple | ✅ Simple |
| Throughput (High Contention) | ✅ High | ⚠️ Lower |

## Real-World Applications

Treiber Stack is widely used in:

- **Concurrent Data Structures**: Foundation for other lock-free structures
- **Memory Management**: Lock-free memory allocators and garbage collectors
- **Task Scheduling**: Work-stealing queues and task schedulers
- **High-Performance Computing**: Parallel algorithms and data structures
- **Runtime Systems**: JVM and language runtime implementations
- **Database Systems**: Transaction management and logging

## Advantages

1. **No Blocking**: Threads never wait for locks, improving responsiveness
2. **High Concurrency**: Multiple threads can operate simultaneously
3. **Deadlock-Free**: No risk of deadlocks since no locks are used
4. **Scalability**: Performance scales well with thread count
5. **Simplicity**: Easy to understand and implement
6. **Progress Guarantee**: Lock-free guarantee ensures system-wide progress

## Considerations

- **ABA Problem**: Theoretical concern, mitigated in garbage-collected languages
- **Memory Reclamation**: Requires careful handling in languages without garbage collection
- **Contention**: High contention can lead to many CAS retries (but no blocking)
- **Non-Linearizable**: Operations may not appear to occur in a global order

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

## Algorithm Visualization

```
Initial State:
head -> null

After pushing 1, 2, 3:
head -> [Node(3)] -> [Node(2)] -> [Node(1)] -> null

Thread A pushes 4:
1. Read head = Node(3)
2. Create Node(4), set next = Node(3)
3. CAS: head.compareAndSet(Node(3), Node(4))
   Success! head -> [Node(4)] -> [Node(3)] -> [Node(2)] -> [Node(1)] -> null

Thread B pops:
1. Read head = Node(4)
2. Read next = Node(3)
3. CAS: head.compareAndSet(Node(4), Node(3))
   Success! head -> [Node(3)] -> [Node(2)] -> [Node(1)] -> null
4. Return 4
```

## Lock-Free vs Wait-Free

**Lock-Free**: At least one thread makes progress (system-wide progress guarantee)
- Treiber Stack is lock-free
- If one thread's CAS fails, another thread's CAS may succeed

**Wait-Free**: Every thread makes progress in a bounded number of steps
- Treiber Stack is NOT wait-free (unbounded retries possible)
- Wait-free algorithms are more complex but provide stronger guarantees

---

**Note**: This implementation demonstrates Treiber Stack using modern Java concurrency utilities. The algorithm is a fundamental building block for high-performance concurrent systems, providing lock-free operations that scale well under contention. Treiber Stack's simplicity and elegance make it an excellent introduction to lock-free programming.

