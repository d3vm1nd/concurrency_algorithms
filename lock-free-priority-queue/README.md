# Lock-Free Priority Queue

## What is a Lock-Free Priority Queue?

A **Lock-Free Priority Queue** is a concurrent data structure that maintains elements in priority order. The highest priority element can be removed efficiently, and new elements can be inserted concurrently while maintaining the priority ordering.

## The Problem It Solves

### Problem 1: Priority Scheduling

Need to process tasks by priority:
- Task schedulers
- Event processing
- Resource allocation

### Problem 2: Concurrent Access

Traditional priority queues with locks:
- Lock contention
- Blocking operations
- Poor scalability

## How It Works

### Data Structure

Uses a **sorted linked list**:
- **Sentinel Head**: Dummy node at the beginning
- **Sorted Order**: Elements sorted by priority (highest first)
- **Atomic References**: CAS operations for thread safety

### Operations

#### Insert Operation

```java
1. Find insertion point (maintain sorted order)
2. Create new node
3. Use CAS to link new node
4. Retry if CAS fails
```

#### Remove Min Operation

```java
1. Get first element (highest priority)
2. Use CAS to unlink first element
3. Return the value
4. Retry if CAS fails
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Priority Ordering**: Maintains priority order
- **Thread-Safe**: Concurrent operations safe
- **Efficient Removal**: O(1) for highest priority element

## Use Cases

### 1. Task Scheduling

Schedule tasks by priority:
- Operating system schedulers
- Task queues
- Event processing

### 2. Resource Allocation

Allocate resources by priority:
- CPU scheduling
- Memory allocation
- I/O scheduling

### 3. Event Processing

Process events by priority:
- Real-time systems
- Game engines
- Simulation systems

## Complexity

- **Insert**: O(n) worst case, O(n) average
- **Remove Min**: O(1) - constant time
- **Peek**: O(1) - constant time
- **Space**: O(n) where n is number of elements

## Advantages

✅ **Lock-Free**: No blocking  
✅ **Priority Ordering**: Maintains order  
✅ **Fast Removal**: O(1) for highest priority  
✅ **Thread-Safe**: Concurrent operations safe  

## Limitations

⚠️ **Linear Insert**: O(n) insertion time  
⚠️ **Comparable Required**: Elements must be comparable  
⚠️ **CAS Retries**: May need retries under contention  

## Improvements

For better performance, consider:
- **Skip List**: O(log n) operations
- **Lock-Free Heap**: More complex but better performance
- **Multi-Level Structures**: Hierarchical priority queues

## Running the Code

```bash
cd lock-free-priority-queue
javac LockFreePriorityQueue.java
java LockFreePriorityQueue
```

## Expected Output

The program demonstrates:
- Multiple threads inserting elements with priorities
- Threads removing highest priority elements
- Priority ordering maintained
- Concurrent operations working correctly

## References

- Lock-free data structure
- Used in task schedulers
- Foundation for more complex priority structures
- Important for concurrent systems

