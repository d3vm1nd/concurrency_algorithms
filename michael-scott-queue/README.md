# Michael-Scott Queue Algorithm

## What is Michael-Scott Queue?

The Michael-Scott Queue is a **lock-free concurrent queue** algorithm that allows multiple threads to enqueue and dequeue elements simultaneously without using locks. It was developed by Maged M. Michael and Michael L. Scott in 1996 and is one of the most widely used lock-free queue implementations in practice.

## The Problem It Solves

Traditional queue implementations using locks have several issues:
- **Lock Contention**: Threads block waiting for locks, reducing throughput
- **Priority Inversion**: High-priority threads can be blocked by low-priority threads holding locks
- **Deadlock Risk**: Complex locking schemes can lead to deadlocks
- **Performance Overhead**: Lock acquisition and release add overhead

Michael-Scott Queue solves these issues by:
- **Lock-Free Operations**: Uses atomic compare-and-swap (CAS) operations instead of locks
- **Non-Blocking**: Threads never block waiting for locks
- **High Throughput**: Multiple threads can operate concurrently
- **No Deadlocks**: Lock-free design eliminates deadlock possibilities

## How It Works

The Michael-Scott Queue maintains a **linked list of nodes** with a dummy node at the head:

### The Queue Structure

- **Dummy Node**: Always present at the head, never contains actual data
- **Head Pointer**: Points to the dummy node (or the node after it during dequeue)
- **Tail Pointer**: Points to the last node in the queue
- **Node Structure**: Each node contains a value and a pointer to the next node

### The Algorithm Steps

#### Enqueue Operation

1. **Create New Node**: Create a new node with the item to enqueue
2. **Read Tail**: Get the current tail node and its next pointer
3. **Verify Consistency**: Check if tail hasn't changed (another thread may have modified it)
4. **Link Node**: 
   - If tail's next is null, try to link the new node using CAS
   - If successful, update tail to point to the new node
   - If tail's next is not null, help advance the tail pointer (another thread is mid-operation)
5. **Retry if Needed**: If CAS fails, retry from step 2

#### Dequeue Operation

1. **Read Head and Tail**: Get current head, tail, and head's next node
2. **Verify Consistency**: Check if head hasn't changed
3. **Check if Empty**:
   - If head equals tail, the queue might be empty
   - If head's next is null, queue is empty, return null
   - Otherwise, help advance tail (another thread is mid-enqueue)
4. **Remove Head**:
   - If queue is not empty, try to move head forward using CAS
   - If successful, return the value from the new head node
5. **Retry if Needed**: If CAS fails, retry from step 1

## Running the Program

```bash
cd michael-scott-queue
javac MichaelScottQueue.java
java MichaelScottQueue
```

## What You'll See

The program creates five virtual threads that each perform multiple enqueue and dequeue operations:

- Each thread enqueues values and then dequeues them
- Operations happen concurrently without locks
- The queue maintains correct ordering despite concurrent access
- The output shows the order of operations and final queue state

## Example Output

```
Thread 0 enqueued: 0
Thread 1 enqueued: 4
Thread 2 enqueued: 8
Thread 0 dequeued: 0
Thread 1 dequeued: 4
Thread 3 enqueued: 12
Thread 2 dequeued: 8
Thread 4 enqueued: 16
Thread 3 dequeued: 12
Thread 4 dequeued: 16
...
Queue operations completed!
Final queue empty: true
```

## Key Features

✅ **Lock-Free**: No locks used, only atomic operations  
✅ **Non-Blocking**: Threads never block waiting for locks  
✅ **Thread-Safe**: Multiple threads can safely enqueue and dequeue concurrently  
✅ **Linearizable**: Operations appear to occur atomically  
✅ **Wait-Free Progress**: At least one thread makes progress  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicReference (`java.util.concurrent.atomic.AtomicReference`)
- Thread-safe reference to head and tail nodes
- `get()`: Atomically reads the current value
- `compareAndSet()`: Conditionally updates only if current value matches expected
- Ensures atomic queue operations without explicit locking

### Compare-and-Swap (CAS)
- Fundamental operation for lock-free programming
- Atomically checks if a value matches expected and updates if true
- If CAS fails, another thread modified the value, so retry
- Provides the building block for all lock-free algorithms

### Virtual Threads (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for high-concurrency scenarios like concurrent queues

### Volatile Fields
- `next` pointer is volatile for visibility guarantees
- Ensures changes are immediately visible to other threads
- Critical for correct lock-free behavior

## Algorithm Complexity

- **Time Complexity**: 
  - Enqueue: O(1) amortized (may retry due to CAS failures)
  - Dequeue: O(1) amortized (may retry due to CAS failures)
- **Space Complexity**: O(n) where n is the number of elements in the queue
- **Cache Behavior**: Good - operations are localized to head and tail

## Comparison with Other Queue Implementations

| Feature | Michael-Scott Queue | Lock-Based Queue | Blocking Queue |
|---------|---------------------|------------------|----------------|
| Lock-Free | ✅ Yes | ❌ No | ❌ No |
| Non-Blocking | ✅ Yes | ❌ No | ❌ No |
| Deadlock Risk | ✅ None | ⚠️ Possible | ⚠️ Possible |
| Throughput | ✅ High | ⚠️ Moderate | ⚠️ Lower |
| Complexity | ⚠️ Medium | ✅ Simple | ✅ Simple |

## Real-World Applications

Michael-Scott Queue is widely used in:

- **High-Performance Systems**: Where lock contention must be minimized
- **Producer-Consumer Patterns**: Multiple producers and consumers
- **Message Passing Systems**: Inter-thread communication
- **Work Stealing**: Task queues in parallel algorithms
- **Event Systems**: Event queues in reactive systems
- **Database Systems**: Transaction queues and work queues
- **Game Engines**: Concurrent event and message queues
- **Network Servers**: Request queues handling concurrent connections

## Advantages

1. **No Locks**: Eliminates lock contention and deadlock risks
2. **High Throughput**: Multiple threads can operate concurrently
3. **Scalability**: Performance scales well with number of threads
4. **Non-Blocking**: Threads never block, improving responsiveness
5. **Wait-Free Progress**: At least one thread always makes progress

## Considerations

- **CAS Retries**: Threads may need to retry operations if CAS fails
- **Memory Reclamation**: In languages without garbage collection, nodes must be carefully managed
- **ABA Problem**: Not an issue here due to dummy node and careful design
- **Complexity**: More complex than lock-based queues

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

## Algorithm Visualization

```
Initial State:
head -> [Dummy: value=null, next=null]
tail -> [Dummy: value=null, next=null]

After enqueue(1):
head -> [Dummy: value=null, next=Node1]
        [Node1: value=1, next=null]
tail -> [Node1: value=1, next=null]

After enqueue(2):
head -> [Dummy: value=null, next=Node1]
        [Node1: value=1, next=Node2]
        [Node2: value=2, next=null]
tail -> [Node2: value=2, next=null]

After dequeue() returns 1:
head -> [Dummy: value=null, next=Node2]
        [Node2: value=2, next=null]
tail -> [Node2: value=2, next=null]
```

## Key Design Decisions

1. **Dummy Node**: Always present at head simplifies empty queue handling
2. **Two-Step Enqueue**: First link node, then update tail (helps other threads)
3. **Helping Mechanism**: Threads help advance tail if they see it's behind
4. **CAS for Atomicity**: All modifications use compare-and-swap for atomicity

## Thread Safety Guarantees

- **Linearizability**: Each operation appears to occur atomically at some point
- **Lock-Free**: System-wide progress guaranteed (at least one thread progresses)
- **Wait-Free**: Individual operations may retry but eventually succeed
- **Memory Consistency**: Volatile fields ensure visibility across threads

---

**Note**: This implementation demonstrates the Michael-Scott Queue using modern Java concurrency utilities. The algorithm is a fundamental building block for high-performance concurrent systems, providing lock-free queue operations that scale well with many threads. It's used in production systems where lock contention must be minimized and high throughput is critical.

