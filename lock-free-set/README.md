# Lock-Free Set

## What is a Lock-Free Set?

A **Lock-Free Set** is a concurrent data structure that maintains a collection of unique elements with lock-free operations. It supports:
- **Add**: Insert an element (if not already present)
- **Remove**: Delete an element
- **Contains**: Check membership

All operations are **lock-free**, meaning threads never block waiting for locks.

## The Problem It Solves

### Problem 1: Lock-Based Sets

Traditional lock-based sets have:
- Lock contention
- Blocking operations
- Poor scalability with many threads

### Problem 2: Concurrent Access

Need thread-safe set operations:
- Multiple threads adding/removing
- No lost updates
- Maintain uniqueness

## How It Works

### Data Structure

Uses a **sorted linked list**:
- **Sentinel Head**: Dummy node at the beginning
- **Sorted Order**: Elements maintained in sorted order
- **Atomic References**: CAS operations for thread safety

### Operations

#### Add Operation

```java
1. Find insertion point (sorted order)
2. Check if element already exists
3. Create new node
4. Use CAS to link new node
5. Retry if CAS fails
```

#### Remove Operation

```java
1. Find node to remove
2. Check if node exists
3. Use CAS to unlink node
4. Retry if CAS fails
```

#### Contains Operation

```java
1. Traverse sorted list
2. Compare values
3. Return true if found, false otherwise
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Sorted**: Elements maintained in order
- **Thread-Safe**: Concurrent operations safe
- **Unique**: No duplicate elements

## Use Cases

### 1. Concurrent Collections

When you need a thread-safe set:
- Shared data structures
- Multi-threaded applications
- Concurrent algorithms

### 2. Membership Testing

Fast membership checks:
- Cache lookups
- Filtering operations
- Deduplication

### 3. Set Operations

Concurrent set operations:
- Union, intersection
- Difference
- Subset checking

## Complexity

- **Add**: O(n) worst case, O(n) average
- **Remove**: O(n) worst case, O(n) average
- **Contains**: O(n) worst case, O(n) average
- **Space**: O(n) where n is number of elements

## Advantages

✅ **Lock-Free**: No blocking  
✅ **Thread-Safe**: Concurrent operations safe  
✅ **Simple**: Straightforward implementation  
✅ **Unique**: Maintains uniqueness  

## Limitations

⚠️ **Linear Search**: O(n) operations (not hash-based)  
⚠️ **Sorted Order**: Requires Comparable elements  
⚠️ **CAS Retries**: May need retries under contention  

## Comparison with Other Sets

| Feature | Lock-Free Set | ConcurrentHashMap | Lock-Based Set |
|---------|---------------|-------------------|----------------|
| **Lock-Free** | Yes | Yes (for most ops) | No |
| **Ordering** | Sorted | Unordered | Depends |
| **Performance** | O(n) | O(1) avg | O(1) avg |
| **Use Case** | Sorted sets | Hash sets | General |

## Running the Code

```bash
cd lock-free-set
javac LockFreeSet.java
java LockFreeSet
```

## Expected Output

The program demonstrates:
- Multiple threads adding elements
- Concurrent membership checks
- Threads removing elements
- Set maintains uniqueness

## Improvements

For better performance, consider:
- **Skip List**: O(log n) operations
- **Hash-Based**: O(1) average operations
- **Tree-Based**: O(log n) operations

## References

- Lock-free data structure
- Based on lock-free linked list principles
- Used in concurrent collections
- Foundation for more complex structures

