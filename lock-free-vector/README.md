# Lock-Free Vector

## What is a Lock-Free Vector?

A **Lock-Free Vector** is a concurrent resizable array that supports dynamic growth while maintaining thread safety through lock-free operations. It allows multiple threads to read and write elements concurrently.

## The Problem It Solves

### Problem 1: Fixed-Size Arrays

Fixed-size arrays have limitations:
- Cannot grow dynamically
- Must know size in advance
- Wasted space or insufficient capacity

### Problem 2: Lock-Based Vectors

Lock-based resizable arrays:
- Lock contention
- Blocking operations
- Poor scalability

## How It Works

### Data Structure

Uses an **atomic reference array**:
- **Atomic Array**: Thread-safe array operations
- **Size Counter**: Atomic integer for current size
- **Dynamic Resizing**: Grows as needed

### Operations

#### Get Operation

```java
1. Check bounds
2. Read from atomic array
3. Return value
```

#### Set Operation

```java
1. Check bounds
2. Write to atomic array
3. Done
```

#### Add Operation

```java
1. Get current size
2. Check if resize needed
3. Resize if necessary
4. Increment size atomically
5. Set value at new index
```

### Resizing

Resizing is complex in lock-free implementations:
- Create new larger array
- Copy elements
- Atomically update array reference
- Old array can be garbage collected

## Key Properties

- **Lock-Free**: No blocking operations (for most operations)
- **Dynamic**: Grows as needed
- **Thread-Safe**: Concurrent operations safe
- **Indexed Access**: O(1) get/set operations

## Use Cases

### 1. Dynamic Collections

When size is unknown:
- Growing lists
- Dynamic buffers
- Accumulators

### 2. Concurrent Data Structures

Base for other structures:
- Hash tables
- Matrices
- Buffers

### 3. High-Performance Systems

When you need indexed access:
- Real-time systems
- Numerical computing
- Data processing

## Complexity

- **Get**: O(1) - constant time
- **Set**: O(1) - constant time
- **Add**: O(1) amortized (O(n) when resizing)
- **Space**: O(n) where n is capacity

## Advantages

✅ **Lock-Free**: No blocking (for most ops)  
✅ **Dynamic**: Grows as needed  
✅ **Fast Access**: O(1) indexed access  
✅ **Thread-Safe**: Concurrent operations safe  

## Limitations

⚠️ **Resizing Complexity**: Full lock-free resizing is complex  
⚠️ **Memory**: May have unused capacity  
⚠️ **Copy Overhead**: Resizing requires copying  

## Implementation Notes

This is a **simplified implementation**. Full lock-free resizable arrays are very complex and typically require:
- Hazard pointers or epoch-based reclamation
- Careful memory management
- Complex resizing algorithms

## Running the Code

```bash
cd lock-free-vector
javac LockFreeVector.java
java LockFreeVector
```

## Expected Output

The program demonstrates:
- Multiple threads adding elements
- Concurrent reads and writes
- Dynamic resizing
- Thread-safe operations

## References

- Lock-free data structure
- Foundation for other concurrent structures
- Used in high-performance systems
- Demonstrates dynamic resizing concepts

