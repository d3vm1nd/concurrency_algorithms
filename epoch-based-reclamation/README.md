# Epoch-Based Reclamation

## What is Epoch-Based Reclamation?

**Epoch-Based Reclamation** is a memory reclamation technique for lock-free data structures. Threads operate in **epochs** (time periods), and memory can only be reclaimed when no threads are in older epochs. This is more efficient than hazard pointers for high-throughput scenarios.

## The Problem It Solves

### Problem 1: Memory Reclamation

In lock-free data structures:
- Nodes may be removed but still referenced
- Cannot immediately free memory
- Need safe reclamation mechanism

### Problem 2: Hazard Pointer Overhead

Hazard pointers have overhead:
- Per-thread hazard pointers
- Checking all threads' pointers
- Performance cost

## How It Works

### Epoch System

Uses **3 epochs** (typically):
- **Current Epoch**: Threads currently operating
- **Previous Epoch**: Threads finishing up
- **Old Epoch**: Safe to reclaim

### Algorithm

1. **Threads enter epoch** before accessing data
2. **Threads exit epoch** after accessing data
3. **Epoch advances** periodically
4. **Memory reclaimed** from old epochs (2 steps back)

### Example

```java
// Thread 1: Accessing data
enterEpoch();  // Enter current epoch
// Access data safely
exitEpoch();   // Exit epoch

// Later: Advance epoch
advanceEpoch();  // Move to next epoch
// Can now reclaim memory from 2 epochs ago
```

## Key Properties

- **Epoch-Based**: Threads operate in epochs
- **Batched Reclamation**: Reclaim in batches
- **Efficient**: Lower overhead than hazard pointers
- **High Throughput**: Good for high-contention scenarios

## Use Cases

### 1. High-Throughput Systems

When performance is critical:
- High-frequency trading
- Real-time systems
- High-performance computing

### 2. Lock-Free Data Structures

Memory reclamation for:
- Lock-free queues
- Lock-free lists
- Lock-free trees

### 3. Long-Running Systems

Systems with continuous operation:
- Database systems
- Web servers
- Streaming systems

## Complexity

- **Enter/Exit Epoch**: O(1) - constant time
- **Advance Epoch**: O(1) - constant time
- **Reclamation**: O(n) where n is objects to reclaim
- **Space**: O(e) where e is number of epochs

## Advantages

✅ **Efficient**: Lower overhead than hazard pointers  
✅ **Batched**: Reclaim in batches  
✅ **High Throughput**: Good for high-contention  
✅ **Simple Concept**: Easy to understand  

## Limitations

⚠️ **Epoch Tracking**: Must track all threads  
⚠️ **Delayed Reclamation**: Memory reclaimed later  
⚠️ **Epoch Count**: Limited number of epochs (typically 3)  

## Comparison with Hazard Pointers

| Feature | Epoch-Based | Hazard Pointers |
|---------|-------------|-----------------|
| **Overhead** | Low | Medium |
| **Reclamation** | Batched | Per-object |
| **Throughput** | High | Medium |
| **Complexity** | Medium | Low |

## Running the Code

```bash
cd epoch-based-reclamation
javac EpochBasedReclamation.java
java EpochBasedReclamation
```

## Expected Output

The program demonstrates:
- Threads entering and exiting epochs
- Epoch advancement
- Memory reclamation from old epochs
- Epoch-based memory management pattern

## Research

Epoch-based reclamation was proposed as an alternative to hazard pointers, offering better performance for high-throughput scenarios.

## References

- Memory reclamation technique for lock-free structures
- Alternative to hazard pointers
- Used in high-performance systems
- Demonstrates epoch-based memory management

