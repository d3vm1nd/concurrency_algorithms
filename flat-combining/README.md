# Flat Combining

## What is Flat Combining?

**Flat Combining** is a synchronization technique where one thread (the **combiner**) executes operations on behalf of other threads. Threads publish their operations to a shared queue, and the combiner processes them in batch, reducing contention and improving cache locality.

## The Problem It Solves

### Problem 1: High Contention

Under high contention:
- Many CAS failures
- Cache line bouncing
- Poor scalability
- Wasted CPU cycles

### Problem 2: Cache Locality

Traditional approaches:
- Each thread accesses shared data
- Poor cache locality
- Cache line invalidation

## How It Works

### Basic Concept

1. **Threads publish operations** to a shared queue
2. **One thread becomes combiner** (first to arrive)
3. **Combiner processes operations** in batch
4. **Results returned** to waiting threads

### Algorithm

```java
1. Thread wants to perform operation
2. Try to become combiner
3. If combiner:
   - Process operations from queue (including own)
   - Execute in batch
4. If not combiner:
   - Publish operation to queue
   - Try to help by becoming combiner
   - Wait for result
```

## Key Properties

- **Batching**: Operations processed in batches
- **Cache Locality**: One thread accesses data
- **Reduced Contention**: Less CAS operations
- **Self-Organizing**: Threads help each other

## Use Cases

### 1. High-Contention Scenarios

When many threads compete:
- Shared counters
- Queues
- Stacks

### 2. Cache-Efficient Systems

When cache locality matters:
- NUMA systems
- High-performance computing
- Memory-bound workloads

### 3. Scalable Data Structures

Improving scalability:
- Concurrent queues
- Concurrent stacks
- Shared data structures

## Complexity

- **Operation**: O(1) average (O(k) worst case where k is batch size)
- **Space**: O(n) where n is pending operations
- **Cache Efficiency**: High (one thread accesses data)

## Advantages

✅ **Reduced Contention**: Batching reduces CAS operations  
✅ **Cache Locality**: One thread accesses data  
✅ **Scalable**: Works well under high contention  
✅ **Self-Helping**: Threads help each other  

## Limitations

⚠️ **Latency**: Operations may wait for combiner  
⚠️ **Fairness**: Combiner may process own operations first  
⚠️ **Complexity**: More complex than simple CAS  

## Comparison with Direct CAS

| Feature | Flat Combining | Direct CAS |
|---------|----------------|------------|
| **Contention** | Low (batched) | High |
| **Cache Locality** | High | Low |
| **Latency** | Higher (batching) | Lower |
| **Best For** | High contention | Low contention |

## Running the Code

```bash
cd flat-combining
javac FlatCombining.java
java FlatCombining
```

## Expected Output

The program demonstrates:
- Multiple threads publishing operations
- Combiner thread processing operations in batch
- Reduced contention through batching
- Correct final counter value

## Research

Flat combining was proposed by Hendler, Incze, Shavit, and Tzafrir in 2010 and has been shown to significantly improve performance under high contention.

## References

- "Flat Combining and the Synchronization-Parallelism Tradeoff" (Hendler et al., 2010)
- Used in high-performance concurrent systems
- Demonstrates batching and cache locality optimization
- Important technique for scalable data structures

