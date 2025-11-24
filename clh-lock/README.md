# CLH Lock Algorithm

## What is CLH Lock?

CLH Lock (Craig, Landin, and Hagersten Lock) is a **fair, queue-based spinlock** algorithm that provides efficient mutual exclusion for concurrent programs. It is one of the most widely used queue-based locks in practice, known for its simplicity and excellent cache performance.

## The Problem It Solves

Traditional spinlocks suffer from several issues:
- **Cache Contention**: All threads spin on the same memory location, causing cache line invalidation
- **Unfairness**: Threads may starve, with some threads never getting the lock
- **Performance Degradation**: High contention leads to poor scalability

CLH Lock solves these issues by:
- **Local Spinning**: Each thread spins on its predecessor's node, reducing cache traffic
- **Fairness**: First-come-first-served queue ensures no thread starves
- **Scalability**: Performance improves with the number of threads
- **Node Reuse**: Efficient memory usage through node recycling

## How It Works

CLH Lock maintains a **queue of waiting threads** using a linked list structure:

### The Queue Structure

Each thread has a **Node** containing:
- `locked`: A boolean flag indicating if the thread is waiting for the lock

The lock maintains:
- `tail`: An atomic reference to the last node in the queue
- Each thread tracks its `currentNode` and `predecessorNode`

### The Algorithm Steps

#### Acquiring the Lock

1. **Get Node**: Get the thread-local node (each thread has its own node)
2. **Set Locked**: Mark your node as locked (waiting for the lock)
3. **Join Queue**: Atomically add yourself to the tail of the queue and get the previous node
4. **Wait for Turn**: Spin on your predecessor's `locked` flag
5. **Lock Acquired**: When predecessor's `locked` becomes false, you have the lock

#### Releasing the Lock

1. **Release Lock**: Set your node's `locked` flag to false
2. **Reuse Node**: Reuse your predecessor's node for future acquisitions (memory efficiency)

## Running the Program

```bash
cd clh-lock
javac CLHLock.java
java CLHLock
```

## What You'll See

The program creates five virtual threads that each try to acquire the lock multiple times:

- Each thread acquires the lock, increments a shared counter, and releases the lock
- The queue ensures threads get the lock in the order they requested it (fairness)
- Each thread spins on its predecessor's node, reducing cache contention
- The output shows the order of execution and final counter value

## Example Output

```
Thread 0 in critical section, counter: 1
Thread 0 in non-critical section
Thread 1 in critical section, counter: 2
Thread 1 in non-critical section
Thread 2 in critical section, counter: 3
Thread 2 in non-critical section
Thread 3 in critical section, counter: 4
Thread 3 in non-critical section
Thread 4 in critical section, counter: 5
Thread 4 in non-critical section
...
Final counter value: 15
```

## Key Features

✅ **Fairness**: First-come-first-served queue prevents starvation  
✅ **Low Contention**: Each thread spins on its predecessor's memory location  
✅ **Scalability**: Performance scales well with many threads  
✅ **Memory Efficiency**: Node reuse reduces memory allocation overhead  
✅ **Lock-Free Operations**: Uses atomic operations for queue management  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicReference (`java.util.concurrent.atomic.AtomicReference`)
- Thread-safe reference to the tail of the queue
- `getAndSet()`: Atomically updates tail and returns previous value
- Ensures atomic queue operations without explicit locking

### ThreadLocal (`java.lang.ThreadLocal`)
- Each thread has its own Node instance for `currentNode`
- Each thread tracks its `predecessorNode` for spinning
- Eliminates allocation overhead during lock acquisition
- Thread-safe and efficient for per-thread data

### Virtual Threads (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for high-concurrency scenarios

### Thread.onSpinWait()
- Modern Java hint for spin-wait loops
- Optimizes CPU usage during spinning
- Introduced in Java 9 for better performance

### Volatile Fields
- `locked` is volatile for visibility guarantees
- Ensures changes are immediately visible to other threads
- Critical for correct lock behavior

## Algorithm Complexity

- **Time Complexity**: O(1) for both acquire and release operations
- **Space Complexity**: O(n) where n is the number of threads (each thread has a node)
- **Cache Behavior**: Excellent - each thread accesses its predecessor's memory location

## Comparison with Other Locks

| Feature | CLH Lock | MCS Lock | Test-and-Set | Bakery Algorithm |
|---------|----------|----------|--------------|------------------|
| Fairness | ✅ Yes | ✅ Yes | ❌ No | ✅ Yes |
| Cache Contention | ✅ Low | ✅ Low | ❌ High | ✅ Low |
| Scalability | ✅ Excellent | ✅ Excellent | ❌ Poor | ⚠️ Moderate |
| Complexity | ✅ Simple | ⚠️ Medium | ✅ Simple | ❌ Complex |
| Node Reuse | ✅ Yes | ❌ No | N/A | N/A |

## CLH vs MCS Lock

Both CLH and MCS are queue-based spinlocks, but they differ in important ways:

**CLH Lock:**
- Threads spin on their **predecessor's** node
- Simpler implementation
- Better for NUMA systems (spins on remote memory)
- Node reuse for memory efficiency

**MCS Lock:**
- Threads spin on their **own** node
- Slightly more complex
- Better for uniform memory access
- Each node needs explicit cleanup

## Real-World Applications

CLH Lock is widely used in:

- **Operating Systems**: Kernel-level synchronization primitives
- **High-Performance Computing**: Parallel algorithms and data structures
- **Database Systems**: Lock managers and transaction processing
- **Concurrent Data Structures**: Lock-free and wait-free algorithms
- **Runtime Systems**: JVM and language runtime implementations
- **NUMA Systems**: Particularly effective on Non-Uniform Memory Access architectures

## Advantages

1. **Fairness**: Guarantees no thread starves
2. **Performance**: Low cache contention improves scalability
3. **Predictability**: First-come-first-served ordering
4. **Efficiency**: Lock-free queue operations
5. **Memory Efficiency**: Node reuse reduces allocations
6. **Simplicity**: Relatively simple to understand and implement

## Considerations

- **Memory Overhead**: Each thread needs a node (minimal with ThreadLocal and node reuse)
- **NUMA Awareness**: Spinning on predecessor's memory may be remote (can be an advantage)
- **Preemption**: If a thread holding the lock is preempted, waiting threads continue spinning

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

## Algorithm Visualization

```
Initial State:
tail -> [Node0: locked=false] (dummy node)

Thread 1 acquires:
tail -> [Node1: locked=true]
         ↑
    [Node0: locked=false] (spins here)

Thread 2 acquires:
tail -> [Node2: locked=true]
         ↑
    [Node1: locked=true] (spins here)
         ↑
    [Node0: locked=false]

Thread 1 releases, Thread 2 gets lock:
tail -> [Node2: locked=false]
         ↑
    [Node1: locked=false] (reused by Thread 1)
```

---

**Note**: This implementation demonstrates CLH Lock using modern Java concurrency utilities. The algorithm is a fundamental building block for high-performance concurrent systems, providing fairness and scalability that simple spinlocks cannot achieve. CLH Lock's simplicity and excellent cache behavior make it a popular choice in many concurrent systems.

