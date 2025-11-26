# Lock-Free Hash Table Algorithm

## What is Lock-Free Hash Table?

The Lock-Free Hash Table is a **concurrent hash table** implementation that allows multiple threads to insert, retrieve, and remove key-value pairs simultaneously without using locks. It uses atomic compare-and-swap (CAS) operations from Java's `java.util.concurrent` package to maintain thread safety.

## The Problem It Solves

Traditional hash table implementations using locks have several issues:
- **Lock Contention**: Threads block waiting for locks, reducing throughput
- **Priority Inversion**: High-priority threads can be blocked by low-priority threads holding locks
- **Deadlock Risk**: Complex locking schemes can lead to deadlocks
- **Performance Overhead**: Lock acquisition and release add overhead
- **Poor Scalability**: Lock contention increases with more threads

Lock-Free Hash Table solves these issues by:
- **Lock-Free Operations**: Uses atomic compare-and-swap (CAS) operations instead of locks
- **Non-Blocking**: Threads never block waiting for locks
- **High Throughput**: Multiple threads can operate concurrently on different buckets
- **No Deadlocks**: Lock-free design eliminates deadlock possibilities
- **Better Scalability**: Reduced contention through bucket-based design

## How It Works

The Lock-Free Hash Table uses a **bucket-based design** with lock-free linked lists:

### The Hash Table Structure

- **Bucket Array**: Fixed-size array of atomic references to bucket heads
- **Bucket Nodes**: Each bucket is a lock-free linked list of nodes
- **Node Structure**: Each node contains a key, value, and pointer to the next node
- **Hash Function**: Maps keys to bucket indices using modulo operation

### The Algorithm Steps

#### Put Operation

1. **Compute Bucket Index**: Calculate which bucket the key belongs to using hash function
2. **Get Bucket Reference**: Get the atomic reference for that bucket
3. **Find Existing Node**: Search the bucket's linked list for an existing node with the same key
4. **Update or Insert**:
   - If node exists, update its value directly (volatile write ensures visibility)
   - If node doesn't exist, create new node and prepend to bucket using CAS
5. **Retry if Needed**: If CAS fails (another thread modified bucket), retry from step 3

#### Get Operation

1. **Compute Bucket Index**: Calculate which bucket the key belongs to
2. **Get Bucket Head**: Get the head node of the bucket's linked list
3. **Search List**: Traverse the linked list to find a node with matching key
4. **Return Value**: If found, return the value; otherwise return null

#### Remove Operation

1. **Compute Bucket Index**: Calculate which bucket the key belongs to
2. **Get Bucket Reference**: Get the atomic reference for that bucket
3. **Check Head Node**:
   - If head matches key, try to update bucket head using CAS
   - If successful, return true
4. **Search Middle Nodes**:
   - Traverse list to find node with matching key
   - Use CAS to unlink the node from its predecessor
5. **Retry if Needed**: If CAS fails, retry from step 2

## Running the Program

```bash
cd lock-free-hash-table
javac LockFreeHashTable.java
java LockFreeHashTable
```

## What You'll See

The program creates five virtual threads that each perform multiple put, get, and remove operations:

- Each thread inserts key-value pairs into the hash table
- Threads retrieve values they inserted
- Some threads remove entries they added
- Operations happen concurrently without locks
- The hash table maintains correct state despite concurrent access

## Example Output

```
Thread 0 put: 0
Thread 1 put: 4
Thread 2 put: 8
Thread 0 got: 0 -> Value-0
Thread 1 got: 4 -> Value-4
Thread 0 removed: 0 -> true
Thread 2 got: 8 -> Value-8
...
Hash table operations completed!
```

## Key Features

✅ **Lock-Free**: No locks used, only atomic operations  
✅ **Non-Blocking**: Threads never block waiting for locks  
✅ **Thread-Safe**: Multiple threads can safely insert, retrieve, and remove concurrently  
✅ **Bucket-Based**: Reduces contention by distributing keys across buckets  
✅ **CAS-Based**: Uses compare-and-swap for atomic updates  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` package and virtual threads

## Implementation Details

This implementation uses modern Java concurrency features:

### AtomicReferenceArray (`java.util.concurrent.atomic.AtomicReferenceArray`)
- Thread-safe array of atomic references to bucket heads
- Each bucket is an `AtomicReference<BucketNode>` pointing to the head of a linked list
- Provides atomic array access without explicit locking

### AtomicReference (`java.util.concurrent.atomic.AtomicReference`)
- Thread-safe reference to bucket head nodes
- `get()`: Atomically reads the current value
- `compareAndSet()`: Conditionally updates only if current value matches expected
- Ensures atomic bucket operations without explicit locking

### Compare-and-Swap (CAS)
- Fundamental operation for lock-free programming
- Atomically checks if a value matches expected and updates if true
- If CAS fails, another thread modified the value, so retry
- Provides the building block for all lock-free algorithms

### Virtual Threads (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for high-concurrency scenarios like concurrent hash tables

### Volatile Fields
- `value` field is volatile for visibility guarantees
- Ensures value updates are immediately visible to other threads
- Critical for correct lock-free behavior

## Algorithm Complexity

- **Time Complexity**: 
  - Put: O(1) average case, O(n) worst case (if all keys hash to same bucket)
  - Get: O(1) average case, O(n) worst case
  - Remove: O(1) average case, O(n) worst case
- **Space Complexity**: O(n) where n is the number of key-value pairs
- **Cache Behavior**: Good - operations are localized to specific buckets

## Comparison with Other Hash Table Implementations

| Feature | Lock-Free Hash Table | Lock-Based Hash Table | ConcurrentHashMap |
|---------|---------------------|----------------------|-------------------|
| Lock-Free | ✅ Yes | ❌ No | ⚠️ Partially |
| Non-Blocking | ✅ Yes | ❌ No | ⚠️ Partially |
| Deadlock Risk | ✅ None | ⚠️ Possible | ✅ None |
| Throughput | ✅ High | ⚠️ Moderate | ✅ High |
| Complexity | ⚠️ Medium | ✅ Simple | ✅ Simple |
| Scalability | ✅ Excellent | ⚠️ Moderate | ✅ Excellent |

## Real-World Applications

Lock-Free Hash Table is used in:

- **High-Performance Systems**: Where lock contention must be minimized
- **Caching Systems**: Concurrent caches requiring fast lookups
- **Database Systems**: Index structures and hash-based indexes
- **Network Servers**: Session management and connection tracking
- **Game Engines**: Entity component systems and lookup tables
- **Real-Time Systems**: Where blocking is unacceptable
- **Distributed Systems**: Local hash tables in distributed data structures

## Advantages

1. **No Locks**: Eliminates lock contention and deadlock risks
2. **High Throughput**: Multiple threads can operate concurrently on different buckets
3. **Scalability**: Performance scales well with number of threads
4. **Non-Blocking**: Threads never block, improving responsiveness
5. **Wait-Free Progress**: At least one thread always makes progress

## Considerations

- **CAS Retries**: Threads may need to retry operations if CAS fails
- **Memory Reclamation**: In languages without garbage collection, nodes must be carefully managed
- **Hash Collisions**: Poor hash function can cause all keys to map to same bucket, degrading to O(n)
- **No Dynamic Resizing**: This implementation uses fixed-size bucket array
- **Complexity**: More complex than lock-based hash tables

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

## Algorithm Visualization

```
Initial State (4 buckets):
Bucket[0]: null
Bucket[1]: null
Bucket[2]: null
Bucket[3]: null

After put(5, "Value-5") (5 % 4 = 1):
Bucket[0]: null
Bucket[1]: [key=5, value="Value-5", next=null]
Bucket[2]: null
Bucket[3]: null

After put(9, "Value-9") (9 % 4 = 1):
Bucket[0]: null
Bucket[1]: [key=9, value="Value-9", next=] -> [key=5, value="Value-5", next=null]
Bucket[2]: null
Bucket[3]: null

After remove(5):
Bucket[0]: null
Bucket[1]: [key=9, value="Value-9", next=null]
Bucket[2]: null
Bucket[3]: null
```

## Key Design Decisions

1. **Bucket-Based Design**: Distributes keys across multiple buckets to reduce contention
2. **Lock-Free Linked Lists**: Each bucket is a lock-free linked list using CAS
3. **Prepend Strategy**: New nodes are prepended to bucket head for O(1) insertion
4. **CAS for Atomicity**: All modifications use compare-and-swap for atomicity
5. **Volatile Values**: Value field is volatile to ensure visibility of updates

## Thread Safety Guarantees

- **Linearizability**: Each operation appears to occur atomically at some point
- **Lock-Free**: System-wide progress guaranteed (at least one thread progresses)
- **Wait-Free**: Individual operations may retry but eventually succeed
- **Memory Consistency**: Volatile fields ensure visibility across threads

---

**Note**: This implementation demonstrates a Lock-Free Hash Table using modern Java concurrency utilities. The algorithm provides lock-free hash table operations that scale well with many threads, making it suitable for high-performance concurrent systems where lock contention must be minimized.

