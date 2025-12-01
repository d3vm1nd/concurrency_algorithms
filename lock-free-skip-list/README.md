# Lock-Free Skip List

## What is a Lock-Free Skip List?

A **Lock-Free Skip List** is a concurrent data structure that provides efficient sorted operations using multiple levels of linked lists with probabilistic balancing. It allows O(log n) average-case search, insert, and delete operations while maintaining thread safety through lock-free mechanisms.

## The Problem It Solves

### Problem 1: Sorted Data Structures

Need efficient sorted operations:
- O(log n) search, insert, delete
- Concurrent access
- No locks

### Problem 2: Tree Alternatives

Skip lists provide:
- Simpler than lock-free trees
- Probabilistic balancing
- Good average-case performance

## How It Works

### Multi-Level Structure

Uses **multiple levels** of linked lists:
- **Level 0**: All elements (sorted)
- **Level 1+**: Subset of elements (probabilistic)
- **Higher levels**: Fewer elements, faster traversal

### Operations

#### Search Operation

```java
1. Start at head, highest level
2. Move right if next value < target
3. Move down if next value >= target
4. Continue until level 0
5. Check if found
```

#### Insert Operation

```java
1. Find insertion point at all levels
2. Generate random level for new node
3. Create new node
4. Link at all levels using CAS
5. Retry if CAS fails
```

#### Delete Operation

```java
1. Find node to delete
2. Mark node at all levels
3. Unlink from all levels
4. Use CAS for atomic unlinking
```

## Key Properties

- **Lock-Free**: No blocking operations
- **Sorted**: Maintains sorted order
- **Probabilistic**: Random level assignment
- **Efficient**: O(log n) average operations

## Use Cases

### 1. Sorted Collections

When you need sorted concurrent data:
- Priority queues
- Sorted sets
- Index structures

### 2. Database Systems

Index structures in databases:
- Secondary indexes
- Range queries
- Ordered access

### 3. High-Performance Systems

When performance matters:
- Real-time systems
- High-frequency systems
- Concurrent algorithms

## Complexity

- **Search**: O(log n) average, O(n) worst case
- **Insert**: O(log n) average, O(n) worst case
- **Delete**: O(log n) average, O(n) worst case
- **Space**: O(n) average

## Advantages

✅ **Lock-Free**: No blocking  
✅ **Efficient**: O(log n) average  
✅ **Sorted**: Maintains order  
✅ **Simpler**: Easier than lock-free trees  

## Limitations

⚠️ **Worst Case**: O(n) worst-case performance  
⚠️ **Random**: Probabilistic structure  
⚠️ **Memory**: Multiple levels use more memory  
⚠️ **Complexity**: More complex than simple lists  

## Comparison with Trees

| Feature | Skip List | Lock-Free Tree |
|---------|-----------|----------------|
| **Complexity** | O(log n) avg | O(log n) |
| **Implementation** | Simpler | More complex |
| **Balancing** | Probabilistic | Deterministic |
| **Worst Case** | O(n) | O(log n) |

## Running the Code

```bash
cd lock-free-skip-list
javac LockFreeSkipList.java
java LockFreeSkipList
```

## Expected Output

The program demonstrates:
- Multiple threads inserting elements
- Concurrent searches
- Threads removing elements
- Skip list maintaining sorted order

## References

- Original skip list by William Pugh (1990)
- Lock-free version for concurrent access
- Used in high-performance systems
- Alternative to lock-free trees

