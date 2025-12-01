# Hazard Pointers

## What are Hazard Pointers?

**Hazard Pointers** are a memory reclamation technique for lock-free data structures. They allow threads to safely reclaim memory that might still be accessed by other threads by maintaining per-thread "hazard pointers" that protect nodes from being reclaimed.

## The Problem It Solves

### Problem: Memory Reclamation in Lock-Free Structures

In lock-free data structures:
- Nodes may be removed but still referenced by other threads
- Cannot immediately free memory (use-after-free risk)
- Need safe way to reclaim memory

### Traditional Solutions

- **Reference Counting**: Overhead, complex
- **Garbage Collection**: Not available in all languages
- **Epoch-Based**: Different approach, also complex

## How It Works

### Basic Concept

Each thread maintains a small set of **hazard pointers**:
- Points to nodes currently being accessed
- Prevents those nodes from being reclaimed
- Other threads check hazard pointers before reclaiming

### Algorithm

1. **Before accessing a node**: Set hazard pointer to that node
2. **Access the node**: Safe to read/write
3. **After access**: Clear hazard pointer
4. **Reclamation**: Only reclaim nodes not in any hazard pointer

### Example

```java
// Thread 1: Accessing node
setHazardPointer(0, node);
value = node.value;  // Safe access
clearHazardPointer(0);

// Thread 2: Reclaiming
if (!isProtected(node)) {
    free(node);  // Safe to free
}
```

## Key Properties

- **Per-Thread**: Each thread has its own hazard pointers
- **Small Set**: Typically 1-4 hazard pointers per thread
- **Safe Reclamation**: Only reclaim unprotected nodes
- **Lock-Free**: No blocking operations

## Use Cases

### 1. Lock-Free Linked Lists

Protect nodes during traversal and modification:
- Harris Linked List
- Lock-free queues
- Lock-free stacks

### 2. Lock-Free Trees

Protect nodes during tree operations:
- Lock-free binary search trees
- Lock-free skip lists

### 3. General Lock-Free Structures

Any lock-free structure needing memory reclamation:
- Hash tables
- Priority queues
- Sets

## Complexity

- **Set/Clear Hazard Pointer**: O(1)
- **Check Protection**: O(k) where k is number of threads
- **Reclamation**: O(n) where n is nodes to check
- **Space**: O(k × h) where k is threads, h is hazard pointers per thread

## Advantages

✅ **Safe**: Prevents use-after-free  
✅ **Lock-Free**: No blocking  
✅ **Efficient**: Small memory overhead  
✅ **Flexible**: Works with any lock-free structure  

## Limitations

⚠️ **Thread Count**: Performance degrades with many threads  
⚠️ **Complexity**: Requires careful implementation  
⚠️ **Reclamation Delay**: Nodes may wait before reclamation  
⚠️ **Per-Thread Overhead**: Each thread needs hazard pointers  

## Comparison with Other Techniques

| Technique | Overhead | Complexity | Best For |
|-----------|----------|------------|----------|
| **Hazard Pointers** | Low | Medium | Lock-free structures |
| **Epoch-Based** | Very Low | High | High-throughput |
| **Reference Counting** | Medium | Low | Simple cases |
| **Garbage Collection** | Variable | Low | Managed languages |

## Running the Code

```bash
cd hazard-pointers
javac HazardPointers.java
java HazardPointers
```

## Real-World Usage

### C++ Standard Library

Some C++ lock-free implementations use hazard pointers for memory management.

### Research

Hazard pointers were proposed by Maged Michael in 2004 and are widely used in high-performance concurrent systems.

## References

- Original paper: "Hazard Pointers: Safe Memory Reclamation for Lock-Free Objects" (Michael, 2004)
- Used in many lock-free data structure implementations
- Important technique for memory-safe lock-free programming

