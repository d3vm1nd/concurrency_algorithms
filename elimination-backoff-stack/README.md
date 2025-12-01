# Elimination Backoff Stack

## What is an Elimination Backoff Stack?

An **Elimination Backoff Stack** is an optimized lock-free stack that uses an **elimination array** to allow concurrent push and pop operations to cancel each other out without accessing the main stack. This reduces contention and significantly improves scalability.

## The Problem It Solves

### Problem 1: Stack Contention

Traditional lock-free stacks have high contention:
- All operations compete for the same top pointer
- CAS failures increase with thread count
- Poor scalability

### Problem 2: CAS Failures

Under high contention:
- Many CAS operations fail
- Threads retry repeatedly
- Wasted CPU cycles

## How It Works

### Two-Level Design

1. **Main Stack**: Standard lock-free stack (Treiber stack)
2. **Elimination Array**: Array of slots for matching operations

### Algorithm

#### Push Operation

```java
1. Try direct push to main stack
2. If fails, try elimination array
3. If elimination succeeds (matched with pop), done
4. If elimination fails, backoff and retry
```

#### Pop Operation

```java
1. Try direct pop from main stack
2. If empty or fails, try elimination array
3. If elimination succeeds (matched with push), done
4. If elimination fails, backoff and retry
```

### Elimination Array

- **Random Slot Selection**: Threads pick random slots
- **Matching**: Push and pop operations can match
- **Timeout**: Operations wait for limited time
- **Backoff**: Exponential backoff on failures

## Key Properties

- **Lock-Free**: No blocking operations
- **High Scalability**: Elimination reduces contention
- **Adaptive**: Uses elimination when contention is high
- **Efficient**: Reduces CAS failures

## Use Cases

### 1. High-Contention Scenarios

When many threads access the stack:
- Producer-consumer systems
- Task schedulers
- Work queues

### 2. Scalable Systems

Systems requiring high throughput:
- Web servers
- Database systems
- Real-time systems

### 3. Load Balancing

When operations can be matched:
- Symmetric workloads
- Balanced push/pop ratios

## Complexity

- **Push/Pop (direct)**: O(1) when no contention
- **Push/Pop (elimination)**: O(1) average with matching
- **Space**: O(n + m) where n is stack size, m is elimination array size

## Advantages

✅ **High Scalability**: Reduces contention  
✅ **Elimination**: Operations can cancel out  
✅ **Adaptive**: Uses elimination when needed  
✅ **Lock-Free**: No blocking  

## Limitations

⚠️ **Complexity**: More complex than simple stack  
⚠️ **Memory**: Elimination array overhead  
⚠️ **Matching Required**: Works best with balanced operations  
⚠️ **Tuning**: Requires parameter tuning  

## Comparison with Treiber Stack

| Feature | Elimination Stack | Treiber Stack |
|---------|-------------------|---------------|
| **Scalability** | Excellent | Good |
| **Contention** | Low (with elimination) | High |
| **Complexity** | High | Low |
| **Best For** | High contention | Low contention |

## Running the Code

```bash
cd elimination-backoff-stack
javac EliminationBackoffStack.java
java EliminationBackoffStack
```

## Expected Output

The program demonstrates:
- Multiple threads pushing and popping
- Elimination array matching operations
- Reduced contention through elimination
- High throughput with many threads

## Research

This algorithm was proposed by Hendler, Shavit, and Yerushalmi in 2004 and is considered one of the most scalable stack implementations.

## References

- "A Scalable Lock-free Stack Algorithm" (Hendler, Shavit, Yerushalmi, 2004)
- Used in high-performance concurrent systems
- Demonstrates elimination technique for contention reduction

