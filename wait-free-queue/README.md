# Wait-Free Queue

## What is a Wait-Free Queue?

A **Wait-Free Queue** is a concurrent queue where **every operation completes in a bounded number of steps**, regardless of other threads' behavior. This is stronger than lock-free, which only guarantees that *some* thread makes progress.

## The Problem It Solves

### Problem 1: Lock-Free Limitations

Lock-free algorithms guarantee:
- At least one thread makes progress
- But individual threads may starve
- No bound on operation time

### Problem 2: Real-Time Requirements

Some systems need:
- Bounded operation time
- No starvation
- Predictable performance

## How It Works

### Helping Mechanism

Wait-free algorithms use **helping**:
- Threads help other threads complete operations
- Ensures every operation eventually completes
- Bounded number of steps

### Algorithm

#### Enqueue Operation

```java
1. Get ticket number
2. Help other threads complete enqueues
3. Perform own enqueue
4. Other threads will help if needed
```

#### Dequeue Operation

```java
1. Try to dequeue
2. Help advance tail if needed
3. Complete own dequeue
4. Bounded number of steps
```

## Key Properties

- **Wait-Free**: Every operation completes in bounded steps
- **No Starvation**: Every thread makes progress
- **Helping**: Threads help each other
- **Bounded Time**: Predictable performance

## Use Cases

### 1. Real-Time Systems

Systems requiring predictable performance:
- Hard real-time systems
- Safety-critical systems
- Embedded systems

### 2. Fairness Requirements

When fairness is critical:
- Resource allocation
- Task scheduling
- Load balancing

### 3. Performance Guarantees

When you need performance bounds:
- Latency-sensitive systems
- Interactive applications
- Time-critical operations

## Complexity

- **Enqueue**: O(k) where k is number of threads (bounded)
- **Dequeue**: O(k) where k is number of threads (bounded)
- **Space**: O(n) where n is number of elements

## Advantages

✅ **Wait-Free**: Bounded operation time  
✅ **No Starvation**: Every thread progresses  
✅ **Predictable**: Performance guarantees  
✅ **Fair**: All threads treated equally  

## Limitations

⚠️ **Complexity**: More complex than lock-free  
⚠️ **Overhead**: Helping mechanism adds overhead  
⚠️ **Bounded Steps**: Steps bounded by thread count  

## Comparison with Lock-Free

| Feature | Wait-Free | Lock-Free |
|---------|-----------|-----------|
| **Progress** | Every thread | Some thread |
| **Starvation** | No | Possible |
| **Time Bound** | Yes | No |
| **Complexity** | Higher | Lower |

## Running the Code

```bash
cd wait-free-queue
javac WaitFreeQueue.java
java WaitFreeQueue
```

## Expected Output

The program demonstrates:
- Multiple threads enqueuing and dequeuing
- Helping mechanism in action
- All operations completing
- Wait-free guarantees

## References

- Stronger progress guarantee than lock-free
- Used in real-time systems
- Important for fairness and predictability
- Demonstrates helping mechanisms

