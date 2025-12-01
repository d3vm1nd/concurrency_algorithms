# Lock-Free Counter

## What is a Lock-Free Counter?

A **Lock-Free Counter** is a high-performance counter implementation that supports multiple concurrent increment and decrement operations using atomic operations. It provides thread-safe counting without locks.

## The Problem It Solves

### Problem 1: Shared Counters

Multiple threads need to update a counter:
- Race conditions with non-atomic operations
- Lost updates
- Incorrect final values

### Problem 2: Performance

Lock-based counters have:
- Lock contention
- Blocking operations
- Poor scalability

## How It Works

### Atomic Operations

Uses **atomic operations** for thread safety:
- **incrementAndGet()**: Atomically increment and return new value
- **decrementAndGet()**: Atomically decrement and return new value
- **addAndGet()**: Atomically add and return new value
- **get()**: Read current value

### Implementation

```java
private final AtomicLong counter = new AtomicLong(0);

public void increment() {
    counter.incrementAndGet();
}
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Thread-Safe**: Concurrent operations safe
- **High Performance**: Hardware-supported operations
- **Scalable**: Works well with many threads

## Use Cases

### 1. Statistics and Metrics

Counting events and metrics:
- Request counters
- Performance metrics
- Statistics collection

### 2. Resource Management

Tracking resources:
- Connection counts
- Memory usage
- Thread counts

### 3. Id Generation

Generating unique IDs:
- Sequence numbers
- Transaction IDs
- Request IDs

### 4. Rate Limiting

Counting operations for rate limiting:
- API rate limits
- Throttling
- Quotas

## Complexity

- **Increment/Decrement**: O(1) - constant time
- **Add**: O(1) - constant time
- **Get**: O(1) - constant time
- **Space**: O(1) - constant space

## Advantages

✅ **Lock-Free**: No blocking  
✅ **Fast**: O(1) operations  
✅ **Thread-Safe**: Concurrent operations safe  
✅ **Simple**: Easy to use  

## Limitations

⚠️ **Contention**: High contention may cause retries (with CAS fallback)  
⚠️ **Single Counter**: One counter per instance  

## Comparison with Lock-Based

| Feature | Lock-Free Counter | Lock-Based Counter |
|---------|-------------------|-------------------|
| **Blocking** | No | Yes |
| **Performance** | High | Lower |
| **Scalability** | Excellent | Good |
| **Complexity** | Low | Low |

## Running the Code

```bash
cd lock-free-counter
javac LockFreeCounter.java
java LockFreeCounter
```

## Expected Output

The program demonstrates:
- Multiple threads incrementing counter
- Concurrent decrements
- Concurrent additions
- Correct final counter value

## Real-World Usage

### Java's Atomic Classes

Java provides:
- `AtomicInteger`
- `AtomicLong`
- `AtomicReference`

### Performance

Lock-free counters are significantly faster than lock-based counters under high contention.

## References

- Fundamental concurrent primitive
- Used in many concurrent systems
- Demonstrates atomic operations
- High-performance counting mechanism

