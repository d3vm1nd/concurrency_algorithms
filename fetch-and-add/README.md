# Fetch-and-Add Algorithm

## What is Fetch-and-Add?

**Fetch-and-Add** is a fundamental atomic operation in concurrent programming that atomically:
1. Reads a value from memory
2. Adds a constant to it
3. Writes the result back
4. Returns the original value

This operation is **atomic**, meaning it appears to execute as a single, indivisible operation from the perspective of other threads.

## The Problem It Solves

Without atomic operations, incrementing a shared counter can lead to **race conditions**:

```java
// NOT thread-safe - race condition!
int value = counter;      // Thread 1 reads: 5
                          // Thread 2 reads: 5 (before Thread 1 writes)
counter = value + 1;      // Thread 1 writes: 6
                          // Thread 2 writes: 6 (lost update!)
```

Fetch-and-Add solves this by making the read-modify-write operation atomic.

## How It Works

### Basic Operation

```java
// Atomic operation
int oldValue = fetchAndAdd(counter, 1);
// Equivalent to:
//   oldValue = counter;
//   counter = counter + 1;
//   return oldValue;
```

### Java Implementation

Java provides fetch-and-add through `AtomicInteger`:

- **`getAndAdd(delta)`**: Returns old value (fetch-and-add)
- **`addAndGet(delta)`**: Returns new value (add-and-fetch)

## Key Properties

- **Atomicity**: Operation is indivisible
- **Thread-Safe**: No race conditions
- **Lock-Free**: No blocking or waiting
- **Efficient**: Hardware-supported on modern CPUs

## Use Cases

### 1. Counters and Accumulators

```java
AtomicInteger counter = new AtomicInteger(0);
int oldValue = counter.getAndAdd(1); // Thread-safe increment
```

### 2. Ticket-Based Systems

```java
// Each thread gets a unique ticket number
int ticket = ticketCounter.getAndAdd(1);
```

### 3. Round-Robin Load Balancing

```java
// Distribute requests across servers
int serverIndex = currentIndex.getAndAdd(1) % numServers;
```

### 4. Memory Allocation

```java
// Allocate memory blocks atomically
int blockAddress = nextFreeBlock.getAndAdd(blockSize);
```

### 5. Sequence Number Generation

```java
// Generate unique sequence numbers
long sequence = sequenceNumber.getAndAdd(1);
```

## Implementation Details

### Hardware Support

Modern CPUs provide fetch-and-add as a single instruction:
- **x86/x64**: `LOCK XADD` instruction
- **ARM**: `LDADD` instruction
- **RISC-V**: Atomic memory operations

### Java Implementation

Java's `AtomicInteger.getAndAdd()` uses:
- **Compare-and-Swap (CAS)** loops on platforms without direct support
- **Hardware fetch-and-add** where available (JVM optimization)

## Comparison with Other Operations

| Operation | Returns | Use Case |
|-----------|---------|----------|
| **Fetch-and-Add** | Old value | Counters, tickets |
| **Add-and-Fetch** | New value | Accumulators |
| **Compare-and-Swap** | Success boolean | Conditional updates |
| **Test-and-Set** | Old value | Locks |

## Complexity

- **Time Complexity**: O(1) - single atomic operation
- **Space Complexity**: O(1) - constant space
- **Scalability**: ✅ Excellent - no contention overhead

## Advantages

✅ **Lock-Free**: No blocking or waiting  
✅ **Fast**: Hardware-supported operation  
✅ **Simple**: Easy to understand and use  
✅ **Scalable**: Works well with many threads  

## Limitations

⚠️ **Contention**: High contention can cause retries (with CAS fallback)  
⚠️ **Limited Operations**: Only supports addition (not arbitrary operations)  

## Running the Code

```bash
cd fetch-and-add
javac FetchAndAdd.java
java FetchAndAdd
```

## Expected Output

The program demonstrates:
- Multiple threads incrementing a shared counter
- No lost updates or race conditions
- Final counter value equals expected value
- Thread-safe operation without locks

## Related Algorithms

- **Compare-and-Swap (CAS)**: More general atomic operation
- **Test-and-Set**: Simpler atomic operation for locks
- **Load-Link/Store-Conditional**: Alternative atomic primitive

## References

- Fundamental operation in concurrent programming
- Used in many lock-free data structures
- Hardware-supported on modern processors
- Essential building block for concurrent algorithms

