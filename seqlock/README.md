# Seqlock (Sequence Lock)

## What is a Seqlock?

A **Seqlock (Sequence Lock)** is a synchronization mechanism optimized for **read-heavy workloads**. It allows:
- **Multiple concurrent readers** without blocking
- **Exclusive writers** that don't block readers
- **Optimistic reading** - readers check for concurrent writes and retry if needed

## The Problem It Solves

### Problem 1: Reader-Writer Locks

Traditional reader-writer locks have issues:
- Readers may block writers
- Writers block all readers
- Lock overhead even for reads

### Problem 2: Read-Heavy Workloads

When reads vastly outnumber writes:
- Need fast, non-blocking reads
- Writers should be rare but exclusive
- Minimize synchronization overhead

## How It Works

### Sequence Number

Uses an **atomic sequence counter**:
- **Even number**: No write in progress
- **Odd number**: Write in progress

### Read Operation

```java
do {
    seq = sequence.get();
    if (seq is odd) continue; // Writer active, retry
    value = data; // Read data
} while (sequence changed or was odd);
```

Readers:
1. Read sequence number
2. If odd, wait (writer active)
3. Read data
4. Re-check sequence number
5. If changed, retry (concurrent write detected)

### Write Operation

```java
sequence.incrementAndGet(); // Make odd
data = newValue;            // Write
sequence.incrementAndGet(); // Make even
```

Writers:
1. Increment sequence (make odd)
2. Perform write
3. Increment sequence again (make even)

## Key Properties

- **Lock-Free Reads**: Readers never acquire locks
- **Optimistic**: Readers retry if write detected
- **Non-Blocking Readers**: Readers never block writers
- **Exclusive Writes**: Only one writer at a time

## Use Cases

### 1. Read-Heavy Data Structures

When reads >> writes:
- Configuration data
- Statistics counters
- Lookup tables

### 2. Linux Kernel

Used extensively in Linux kernel:
- System time
- Network statistics
- Process statistics

### 3. High-Performance Systems

Where read performance is critical:
- Real-time systems
- High-frequency trading
- Monitoring systems

## Complexity

- **Read**: O(1) average, O(k) worst case (k = number of retries)
- **Write**: O(1) - constant time
- **Space**: O(1) - constant space

## Advantages

✅ **Fast Reads**: No locking overhead  
✅ **Non-Blocking**: Readers don't block writers  
✅ **Scalable**: Many concurrent readers  
✅ **Simple**: Easy to understand and implement  

## Limitations

⚠️ **Writer Starvation**: Writers may wait if many readers  
⚠️ **Retries**: Readers may need to retry  
⚠️ **Not Wait-Free**: Readers may retry indefinitely (theoretically)  
⚠️ **Read-Heavy Only**: Not suitable for write-heavy workloads  

## Comparison with Reader-Writer Locks

| Feature | Seqlock | Reader-Writer Lock |
|---------|---------|-------------------|
| **Read Lock** | None (optimistic) | Acquire lock |
| **Write Lock** | Sequence number | Acquire exclusive lock |
| **Reader Blocking** | Never | May block writers |
| **Writer Blocking** | None (exclusive) | Blocks all readers |
| **Best For** | Read-heavy | Balanced workloads |

## Running the Code

```bash
cd seqlock
javac Seqlock.java
java Seqlock
```

## Expected Output

The program demonstrates:
- Multiple readers reading concurrently
- Writers updating data exclusively
- Readers detecting and handling concurrent writes
- High read throughput

## Real-World Usage

### Linux Kernel

```c
// Linux kernel seqlock usage
do {
    seq = read_seqbegin(&seqlock);
    // Read protected data
} while (read_seqretry(&seqlock, seq));
```

### Java Implementation

Java doesn't have built-in seqlock, but the pattern can be implemented using `AtomicInteger` for the sequence counter.

## References

- Used in Linux kernel for high-performance reads
- Optimistic concurrency control pattern
- Suitable for read-heavy concurrent data structures

