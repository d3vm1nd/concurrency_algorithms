# Lock-Free Deque

## What is a Lock-Free Deque?

A **Lock-Free Deque (Double-Ended Queue)** is a concurrent data structure that allows operations on both ends:
- **Push/Pop Left**: Add or remove elements from the left end
- **Push/Pop Right**: Add or remove elements from the right end

All operations are **lock-free**, meaning threads never block waiting for locks.

## The Problem It Solves

### Problem 1: Single-Ended Queues

Regular queues only support operations at one end:
- Limited flexibility
- Cannot efficiently support certain algorithms (e.g., work-stealing)

### Problem 2: Lock-Based Deques

Lock-based implementations have:
- Lock contention
- Blocking operations
- Poor scalability

## How It Works

### Data Structure

Uses a **doubly-linked list** with sentinel nodes:
- **Left Sentinel**: Dummy node at the left end
- **Right Sentinel**: Dummy node at the right end
- **Nodes**: Contain value, prev, and next references

### Operations

#### Push Left
```java
1. Create new node
2. Link between left sentinel and first node
3. Use CAS to atomically update links
```

#### Push Right
```java
1. Create new node
2. Link between last node and right sentinel
3. Use CAS to atomically update links
```

#### Pop Left/Right
```java
1. Find first/last element
2. Unlink it from the deque
3. Use CAS to atomically update links
4. Return the value
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Bidirectional**: Operations on both ends
- **Thread-Safe**: Concurrent operations are safe
- **Scalable**: Works well with many threads

## Use Cases

### 1. Work-Stealing

Work-stealing schedulers use deques:
- Owner pushes/pops from one end (fast)
- Thieves steal from the other end

### 2. Producer-Consumer

Multiple producers and consumers:
- Producers push to one end
- Consumers pop from the other end

### 3. Algorithm Support

Some algorithms require deque operations:
- Breadth-first search
- Sliding window algorithms
- Palindrome checking

## Complexity

- **Push Left/Right**: O(1) amortized
- **Pop Left/Right**: O(1) amortized
- **Space**: O(n) where n is number of elements

## Advantages

✅ **Lock-Free**: No blocking  
✅ **Bidirectional**: Operations on both ends  
✅ **Flexible**: Supports various use cases  
✅ **Scalable**: Good performance with many threads  

## Limitations

⚠️ **Complexity**: More complex than single-ended queue  
⚠️ **Memory**: Doubly-linked list overhead  
⚠️ **CAS Retries**: May need retries under contention  

## Running the Code

```bash
cd lock-free-deque
javac LockFreeDeque.java
java LockFreeDeque
```

## References

- Used in work-stealing schedulers
- Fundamental concurrent data structure
- Lock-free implementation using CAS

