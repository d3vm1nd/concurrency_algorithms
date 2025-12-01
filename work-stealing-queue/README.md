# Work-Stealing Queue

## What is a Work-Stealing Queue?

A **Work-Stealing Queue** is a lock-free concurrent data structure optimized for scenarios where:
- Each thread has its own queue (deque)
- Threads primarily operate on their own queue (fast path)
- When a thread's queue is empty, it can "steal" work from other threads' queues
- Reduces contention compared to a single shared queue

This data structure is fundamental to **work-stealing schedulers** used in parallel computing frameworks.

## The Problem It Solves

### Problem 1: Contention in Shared Queue

A single shared queue creates contention:
- All threads compete for the same queue
- High synchronization overhead
- Poor scalability with many threads

### Problem 2: Load Imbalance

Some threads may finish work quickly while others have many tasks:
- Idle threads waste CPU resources
- Need dynamic load balancing

## How It Works

### Data Structure

Each thread has a **double-ended queue (deque)**:
- **Bottom**: Owner thread pushes/pops from here (fast path, no synchronization)
- **Top**: Other threads steal from here (uses CAS for synchronization)

### Operations

#### 1. Push (Owner Thread)
```java
// Fast path - no synchronization needed
push(task) {
    tasks[bottom] = task;
    bottom++;
}
```

#### 2. Pop (Owner Thread)
```java
// Fast path - optimized for owner
pop() {
    bottom--;
    if (bottom > top) {
        return tasks[bottom];
    }
    // Last element - coordinate with stealers
    if (top == bottom) {
        return tasks[bottom];
    }
    return null; // Someone stole it
}
```

#### 3. Steal (Other Threads)
```java
// Uses CAS for synchronization
steal() {
    if (top >= bottom) return null;
    task = tasks[top];
    if (CAS(top, top+1)) {
        return task;
    }
    return null; // Race condition
}
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Fast Owner Path**: Owner operations are very fast (no CAS)
- **Load Balancing**: Idle threads steal work from busy threads
- **Scalable**: Reduces contention compared to shared queue

## Use Cases

### 1. Fork-Join Framework

Java's `ForkJoinPool` uses work-stealing:
- Each worker thread has its own deque
- Threads steal tasks when their queue is empty
- Enables efficient parallel processing

### 2. Task Schedulers

Parallel task execution:
- Distribute tasks to thread queues
- Threads process their own tasks
- Steal when idle for load balancing

### 3. Parallel Algorithms

Divide-and-conquer algorithms:
- Recursive task decomposition
- Dynamic work distribution
- Efficient parallel execution

### 4. Event Processing

High-throughput event processing:
- Each thread processes its own events
- Steal events when queue is empty
- Maintains high throughput

## Implementation Details

### Circular Buffer

Uses a circular buffer (array) for efficiency:
- Capacity must be power of 2 for fast modulo
- Uses bitwise AND (`&`) instead of modulo
- Atomic references for thread-safe access

### Index Management

- **Bottom**: Grows downward (owner operations)
- **Top**: Grows upward (stealing operations)
- Size = `bottom - top`

### Race Conditions

Handled carefully:
- Owner and stealer can operate concurrently
- CAS operations prevent lost updates
- Last element requires special handling

## Complexity

- **Push**: O(1) - constant time (no synchronization)
- **Pop**: O(1) - constant time (no synchronization for owner)
- **Steal**: O(1) - constant time (CAS operation)
- **Space**: O(n) - where n is capacity

## Advantages

✅ **Low Contention**: Each thread has its own queue  
✅ **Fast Owner Path**: No synchronization for owner operations  
✅ **Load Balancing**: Automatic work distribution  
✅ **Scalable**: Works well with many threads  

## Limitations

⚠️ **Memory Overhead**: Each thread needs its own queue  
⚠️ **Stealing Overhead**: CAS operations for stealing  
⚠️ **Capacity Limits**: Fixed capacity (must be power of 2)  

## Comparison with Other Queues

| Queue Type | Owner Operations | Stealing | Contention |
|------------|------------------|----------|------------|
| **Work-Stealing** | O(1) no sync | O(1) CAS | Low |
| **Michael-Scott** | O(1) CAS | N/A | Medium |
| **Lock-Based** | O(1) lock | N/A | High |

## Running the Code

```bash
cd work-stealing-queue
javac WorkStealingQueue.java
java WorkStealingQueue
```

## Expected Output

The program demonstrates:
- Threads pushing tasks to their own queues
- Threads processing their own tasks
- Threads stealing tasks when their queue is empty
- Load balancing across threads
- All tasks completed correctly

## Real-World Usage

### Java ForkJoinPool

```java
ForkJoinPool pool = new ForkJoinPool();
pool.submit(() -> {
    // Tasks are distributed using work-stealing
});
```

### Parallel Streams

Java parallel streams use work-stealing internally for task distribution.

## References

- Used in Java's ForkJoinPool
- Fundamental to parallel computing
- Research: "Scheduling Multithreaded Computations by Work Stealing" (Blumofe & Leiserson, 1999)
- Efficient load balancing mechanism

