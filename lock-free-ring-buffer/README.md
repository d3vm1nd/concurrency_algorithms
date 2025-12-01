# Lock-Free Ring Buffer

## What is a Lock-Free Ring Buffer?

A **Lock-Free Ring Buffer** (also called Circular Buffer) is a bounded concurrent data structure that:
- Has a **fixed capacity**
- Supports **multiple producers and consumers**
- Uses **lock-free operations** for thread safety
- Provides **backpressure** when full

## The Problem It Solves

### Problem 1: Unbounded Queues

Unbounded queues can:
- Grow indefinitely (memory issues)
- No backpressure mechanism
- Difficult to control resource usage

### Problem 2: Lock-Based Buffers

Lock-based implementations:
- Lock contention
- Blocking operations
- Poor scalability

## How It Works

### Data Structure

Uses a **circular array** with atomic indices:
- **Array**: Fixed-size buffer
- **Write Index**: Next position to write (producer)
- **Read Index**: Next position to read (consumer)
- **Mask**: For efficient modulo (capacity must be power of 2)

### Operations

#### Enqueue (Producer)

```java
1. Get current write index
2. Calculate next write index
3. Check if buffer is full
4. CAS to advance write index
5. Write item to buffer
```

#### Dequeue (Consumer)

```java
1. Get current read index
2. Check if buffer is empty
3. Read item from buffer
4. CAS to advance read index
5. Clear the slot
```

## Key Properties

- **Bounded**: Fixed capacity prevents unbounded growth
- **Lock-Free**: No blocking operations
- **Multiple Producers/Consumers**: Concurrent access supported
- **Backpressure**: Full buffer signals producers to wait

## Use Cases

### 1. Producer-Consumer Patterns

Bounded communication between threads:
- Task queues
- Event processing
- Message passing

### 2. Real-Time Systems

Fixed memory footprint:
- Embedded systems
- Real-time processing
- Audio/video buffers

### 3. High-Performance Systems

Low-latency communication:
- Network packet buffers
- Log buffers
- Performance monitoring

## Complexity

- **Enqueue**: O(1) - constant time
- **Dequeue**: O(1) - constant time
- **Space**: O(n) where n is capacity

## Advantages

✅ **Bounded**: Fixed memory usage  
✅ **Lock-Free**: No blocking  
✅ **Fast**: O(1) operations  
✅ **Backpressure**: Natural flow control  

## Limitations

⚠️ **Fixed Size**: Cannot grow dynamically  
⚠️ **Power of 2**: Capacity must be power of 2 for efficiency  
⚠️ **Waste Space**: May have unused slots  
⚠️ **Full/Empty Detection**: Requires careful index management  

## Implementation Details

### Power of 2 Capacity

Capacity must be power of 2 to use bitwise AND instead of modulo:
```java
index & mask  // Instead of index % capacity
```

This is much faster than modulo operation.

### Index Management

- **Write Index**: Points to next write position
- **Read Index**: Points to next read position
- **Full Condition**: `(writeIndex + 1) % capacity == readIndex`
- **Empty Condition**: `writeIndex == readIndex`

## Running the Code

```bash
cd lock-free-ring-buffer
javac LockFreeRingBuffer.java
java LockFreeRingBuffer
```

## Expected Output

The program demonstrates:
- Multiple producers enqueuing items
- Multiple consumers dequeuing items
- Backpressure when buffer is full
- All items produced are consumed

## Comparison with Other Queues

| Feature | Ring Buffer | Michael-Scott Queue |
|---------|-------------|---------------------|
| **Capacity** | Fixed (bounded) | Unbounded |
| **Memory** | Fixed | Grows |
| **Backpressure** | Yes | No |
| **Use Case** | Real-time, embedded | General purpose |

## References

- Used in real-time systems
- Common in embedded programming
- Provides bounded communication channel
- Lock-free implementation using CAS

