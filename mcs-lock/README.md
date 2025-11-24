# MCS Lock Algorithm

## What is MCS Lock?

MCS Lock (Mellor-Crummey and Scott Lock) is a **fair, queue-based spinlock** algorithm that provides efficient mutual exclusion for concurrent programs. Unlike simple spinlocks that cause high cache contention, MCS Lock reduces contention by having each thread spin on its own local variable.

## The Problem It Solves

Traditional spinlocks have a critical flaw:
- **Cache Contention**: All threads spin on the same memory location, causing cache line invalidation
- **Unfairness**: Threads may starve, with some threads never getting the lock
- **Performance Degradation**: High contention leads to poor scalability

MCS Lock solves these issues by:
- **Local Spinning**: Each thread spins on its own node, reducing cache traffic
- **Fairness**: First-come-first-served queue ensures no thread starves
- **Scalability**: Performance improves with the number of threads

## How It Works

MCS Lock maintains a **queue of waiting threads** using a linked list structure:

### The Queue Structure

Each thread has a **Node** containing:
- `waiting`: A boolean flag indicating if the thread is waiting for the lock
- `next`: A pointer to the next thread in the queue

The lock maintains:
- `tail`: An atomic reference to the last node in the queue

### The Algorithm Steps

#### Acquiring the Lock

1. **Create/Get Node**: Get the thread-local node (each thread has its own node)
2. **Join Queue**: Atomically add yourself to the tail of the queue
3. **Wait for Turn**: 
   - If you're the first thread (previous tail was null), you get the lock immediately
   - Otherwise, set the previous node's `next` pointer to yourself and spin on your `waiting` flag
4. **Lock Acquired**: When `waiting` becomes false, you have the lock

#### Releasing the Lock

1. **Check for Next Thread**: Look at your node's `next` pointer
2. **Pass Lock**: 
   - If there's a next thread, set their `waiting` flag to false (passing the lock)
   - If no next thread, try to set tail to null (indicating queue is empty)
   - If tail changed (another thread joined), wait for them to set your `next` pointer, then pass the lock
3. **Clean Up**: Reset your node's `next` pointer

## Running the Program

```bash
cd mcs-lock
javac MCSLock.java
java MCSLock
```

## What You'll See

The program creates five virtual threads that each try to acquire the lock multiple times:

- Each thread acquires the lock, increments a shared counter, and releases the lock
- The queue ensures threads get the lock in the order they requested it (fairness)
- Each thread spins on its own local variable, reducing cache contention
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
✅ **Low Contention**: Each thread spins on its own memory location  
✅ **Scalability**: Performance scales well with many threads  
✅ **Lock-Free Operations**: Uses atomic operations for queue management  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicReference (`java.util.concurrent.atomic.AtomicReference`)
- Thread-safe reference to the tail of the queue
- `getAndSet()`: Atomically updates tail and returns previous value
- `compareAndSet()`: Conditionally updates tail only if it matches expected value
- Ensures atomic queue operations without explicit locking

### ThreadLocal (`java.lang.ThreadLocal`)
- Each thread has its own Node instance
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
- `waiting` and `next` are volatile for visibility guarantees
- Ensures changes are immediately visible to other threads
- Critical for correct lock behavior

## Algorithm Complexity

- **Time Complexity**: O(1) for both acquire and release operations
- **Space Complexity**: O(n) where n is the number of threads (each thread has a node)
- **Cache Behavior**: Excellent - each thread accesses different memory locations

## Comparison with Other Locks

| Feature | MCS Lock | Test-and-Set | Bakery Algorithm |
|---------|----------|--------------|------------------|
| Fairness | ✅ Yes | ❌ No | ✅ Yes |
| Cache Contention | ✅ Low | ❌ High | ✅ Low |
| Scalability | ✅ Excellent | ❌ Poor | ⚠️ Moderate |
| Complexity | ⚠️ Medium | ✅ Simple | ❌ Complex |

## Real-World Applications

MCS Lock is widely used in:

- **Operating Systems**: Kernel-level synchronization primitives
- **High-Performance Computing**: Parallel algorithms and data structures
- **Database Systems**: Lock managers and transaction processing
- **Concurrent Data Structures**: Lock-free and wait-free algorithms
- **Runtime Systems**: JVM and language runtime implementations

## Advantages

1. **Fairness**: Guarantees no thread starves
2. **Performance**: Low cache contention improves scalability
3. **Predictability**: First-come-first-served ordering
4. **Efficiency**: Lock-free queue operations

## Considerations

- **Memory Overhead**: Each thread needs a node (minimal with ThreadLocal)
- **Complexity**: More complex than simple spinlocks
- **NUMA Systems**: May need adjustments for Non-Uniform Memory Access architectures

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

## Algorithm Visualization

```
Initial State:
tail -> null

Thread 0 acquires:
tail -> [Node0: waiting=false, next=null]

Thread 1 acquires:
tail -> [Node1: waiting=true, next=null]
         ↑
    [Node0: waiting=false, next=Node1]

Thread 0 releases, Thread 1 gets lock:
tail -> [Node1: waiting=false, next=null]
```

---

**Note**: This implementation demonstrates MCS Lock using modern Java concurrency utilities. The algorithm is a fundamental building block for high-performance concurrent systems, providing fairness and scalability that simple spinlocks cannot achieve.

