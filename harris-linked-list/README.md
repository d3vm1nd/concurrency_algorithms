# Harris's Lock-Free Linked List Algorithm

## What is Harris's Algorithm?

Harris's Algorithm is a **lock-free concurrent linked list** implementation that allows multiple threads to insert, delete, and search elements simultaneously without using locks. It was developed by Timothy L. Harris in 2001 and is one of the most efficient lock-free linked list algorithms.

## The Problem It Solves

Traditional linked list implementations using locks have several issues:
- **Lock Contention**: Threads block waiting for locks, reducing throughput
- **Poor Scalability**: Lock-based approaches don't scale well with many threads
- **Deadlock Risk**: Complex locking schemes can lead to deadlocks
- **Performance Overhead**: Lock acquisition and release add significant overhead

Harris's Algorithm solves these issues by:
- **Lock-Free Operations**: Uses atomic compare-and-swap (CAS) operations instead of locks
- **Non-Blocking**: Threads never block waiting for locks
- **High Throughput**: Multiple threads can operate concurrently
- **No Deadlocks**: Lock-free design eliminates deadlock possibilities

## How It Works

Harris's Algorithm maintains a **sorted linked list** with a two-phase deletion strategy:

### The List Structure

- **Head Node**: Dummy node that always exists at the beginning
- **Sorted Order**: Nodes are maintained in sorted order (ascending)
- **Marked Nodes**: Deleted nodes are marked but not immediately unlinked
- **Node Structure**: Each node contains a value and a marked reference to the next node

### The Algorithm Steps

#### Add Operation

1. **Find Window**: Locate the correct position (predecessor and current nodes)
2. **Check Existence**: Verify the value doesn't already exist
3. **Create Node**: Create a new node with the value
4. **Link Node**: Use CAS to link the new node between predecessor and current
5. **Retry if Needed**: If CAS fails, retry from step 1

#### Remove Operation

1. **Find Window**: Locate the node to remove (predecessor and current nodes)
2. **Check Existence**: Verify the node exists
3. **Mark Node**: Mark the node as deleted using CAS (first phase)
4. **Unlink Node**: Unlink the marked node by updating predecessor's next pointer (second phase)
5. **Retry if Needed**: If CAS fails, retry from step 1

#### Contains Operation

1. **Traverse List**: Start from head and traverse the list
2. **Skip Marked**: Skip any nodes that are marked as deleted
3. **Compare Values**: Compare values until found or end of list
4. **Return Result**: Return true if found and not marked, false otherwise

### Two-Phase Deletion

The key innovation of Harris's algorithm is **two-phase deletion**:

1. **Mark Phase**: Mark the node as deleted (logical deletion)
2. **Unlink Phase**: Physically remove the node from the list

This approach ensures that:
- Other threads can still traverse the list even during deletion
- The deletion is atomic and safe
- No thread can miss nodes during traversal

## Running the Program

```bash
cd harris-linked-list
javac HarrisLinkedList.java
java HarrisLinkedList
```

## What You'll See

The program creates five virtual threads that each perform multiple add, contains, and remove operations:

- Each thread adds values to the list
- Threads search for values they added
- Some threads remove values they added
- Operations happen concurrently without locks
- The list maintains correct ordering despite concurrent access

## Example Output

```
Thread 0 added: 0
Thread 1 added: 4
Thread 0 found 0: true
Thread 2 added: 8
Thread 0 removed 0: true
Thread 1 found 4: true
Thread 3 added: 12
Thread 2 found 8: true
Thread 4 added: 16
Thread 1 removed 4: true
...
List operations completed!
```

## Key Features

✅ **Lock-Free**: No locks used, only atomic operations  
✅ **Non-Blocking**: Threads never block waiting for locks  
✅ **Thread-Safe**: Multiple threads can safely insert, delete, and search concurrently  
✅ **Linearizable**: Operations appear to occur atomically  
✅ **Wait-Free Progress**: At least one thread makes progress  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicMarkableReference (`java.util.concurrent.atomic.AtomicMarkableReference`)

- Thread-safe reference that can be marked (for deletion)
- `getReference()`: Atomically reads the current node reference
- `isMarked()`: Checks if the reference is marked
- `compareAndSet()`: Conditionally updates reference and mark only if current values match
- Perfect for implementing two-phase deletion in lock-free linked lists

### Compare-and-Swap (CAS)

- Fundamental operation for lock-free programming
- Atomically checks if a value matches expected and updates if true
- If CAS fails, another thread modified the value, so retry
- Provides the building block for all lock-free algorithms

### Virtual Threads (`Thread.ofVirtual()`)

- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for high-concurrency scenarios like concurrent data structures

### Window Finding

- The `findWindow()` method locates the correct position for insertion/deletion
- Automatically removes marked nodes during traversal (helping mechanism)
- Ensures the list remains consistent even with concurrent modifications

## Algorithm Complexity

- **Time Complexity**: 
  - Add: O(n) worst case, O(1) average (may retry due to CAS failures)
  - Remove: O(n) worst case, O(1) average (may retry due to CAS failures)
  - Contains: O(n) worst case, O(1) average
- **Space Complexity**: O(n) where n is the number of elements in the list
- **Cache Behavior**: Good - operations traverse the list sequentially

## Comparison with Other List Implementations

| Feature | Harris's Algorithm | Lock-Based List | Skip List |
|---------|-------------------|-----------------|-----------|
| Lock-Free | ✅ Yes | ❌ No | ⚠️ Depends |
| Non-Blocking | ✅ Yes | ❌ No | ⚠️ Depends |
| Deadlock Risk | ✅ None | ⚠️ Possible | ⚠️ Possible |
| Throughput | ✅ High | ⚠️ Moderate | ⚠️ Moderate |
| Complexity | ⚠️ Medium | ✅ Simple | 🔴 Complex |
| Sorted | ✅ Yes | ⚠️ Optional | ✅ Yes |

## Real-World Applications

Harris's Algorithm is widely used in:

- **High-Performance Systems**: Where lock contention must be minimized
- **Concurrent Data Structures**: Building blocks for other lock-free structures
- **Database Systems**: Index structures and sorted collections
- **Memory Management**: Lock-free memory allocators
- **Operating Systems**: Kernel data structures requiring high concurrency
- **Game Engines**: Concurrent entity management
- **Network Servers**: Connection management and routing tables

## Advantages

1. **No Locks**: Eliminates lock contention and deadlock risks
2. **High Throughput**: Multiple threads can operate concurrently
3. **Scalability**: Performance scales well with number of threads
4. **Non-Blocking**: Threads never block, improving responsiveness
5. **Wait-Free Progress**: At least one thread always makes progress
6. **Sorted Order**: Maintains sorted order for efficient searching

## Considerations

- **CAS Retries**: Threads may need to retry operations if CAS fails
- **Memory Reclamation**: In languages without garbage collection, nodes must be carefully managed
- **Linear Search**: Search operations are O(n) in worst case
- **Complexity**: More complex than lock-based lists
- **Marked Nodes**: Deleted nodes remain in memory until unlinked (handled by GC in Java)

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations
- Elements must implement `Comparable<T>` interface for sorting

## Algorithm Visualization

```
Initial State:
head -> [Dummy: value=null, next=null]

After add(5):
head -> [Dummy: value=null, next=Node5]
        [Node5: value=5, next=null, marked=false]

After add(3):
head -> [Dummy: value=null, next=Node3]
        [Node3: value=3, next=Node5, marked=false]
        [Node5: value=5, next=null, marked=false]

After remove(3):
head -> [Dummy: value=null, next=Node5]
        [Node3: value=3, next=Node5, marked=true]  (marked but not yet unlinked)
        [Node5: value=5, next=null, marked=false]

After unlink completes:
head -> [Dummy: value=null, next=Node5]
        [Node5: value=5, next=null, marked=false]
```

## Key Design Decisions

1. **Sorted List**: Maintains sorted order for efficient searching
2. **Two-Phase Deletion**: Mark first, then unlink (ensures safety)
3. **Helping Mechanism**: Threads help unlink marked nodes during traversal
4. **Window Finding**: Locates correct position while cleaning up marked nodes
5. **CAS for Atomicity**: All modifications use compare-and-swap for atomicity

## Thread Safety Guarantees

- **Linearizability**: Each operation appears to occur atomically at some point
- **Lock-Free**: System-wide progress guaranteed (at least one thread progresses)
- **Wait-Free**: Individual operations may retry but eventually succeed
- **Memory Consistency**: AtomicMarkableReference ensures visibility across threads

---

**Note**: This implementation demonstrates Harris's Algorithm using modern Java concurrency utilities. The algorithm is a fundamental building block for high-performance concurrent systems, providing lock-free list operations that scale well with many threads. It's used in production systems where lock contention must be minimized and high throughput is critical.

