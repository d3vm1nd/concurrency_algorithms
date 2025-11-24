# Compare-and-Swap (CAS) Algorithm

## What is Compare-and-Swap (CAS)?

Compare-and-Swap (CAS) is a fundamental atomic operation used in concurrent programming to implement lock-free data structures and algorithms. It allows a thread to atomically update a value only if it matches an expected value, without using traditional locks.

## The Problem It Solves

When multiple threads need to update shared data:
- **Without protection**: Threads might overwrite each other's changes, causing lost updates and data corruption
- **With CAS**: Threads can safely update shared data by checking if the value hasn't changed before updating it

## How It Works

CAS uses an atomic operation that performs three steps in a single, uninterruptible operation:

### **The CAS Operation**
The `compareAndSet(expectedValue, newValue)` method atomically:
1. **Reads** the current value from memory
2. **Compares** it with the expected value
3. **Swaps** to the new value only if the comparison succeeds
4. **Returns** `true` if the swap happened, `false` otherwise

All of this happens in a single, atomic step that cannot be interrupted by other threads.

## The Algorithm Steps

When a thread wants to update a shared value:

1. **Read the current value**: Get the current value from the atomic variable
2. **Calculate the new value**: Based on the current value, compute what the new value should be
3. **Attempt CAS**: Try to atomically swap the old value with the new value
   - If CAS succeeds (`true`): The update was successful! The value has been changed
   - If CAS fails (`false`): Another thread changed the value first, go to step 4
4. **Retry if needed**: If CAS failed, read the new current value and try again (this is called a "retry loop")

## Why It Works

- **Atomicity**: The entire read-compare-write operation happens atomically
- **Lock-Free**: No locks are needed, threads don't block each other
- **Optimistic Concurrency**: Threads assume no conflict, but retry if one occurs
- **Progress**: At least one thread will succeed on each attempt

## Running the Program

```bash
cd compare-and-swap
javac CASAlgorithm.java
java CASAlgorithm
```

## What You'll See

The program creates 4 threads that each try to:
- Increment a shared counter 3 times using CAS
- Update shared data 3 times using CAS

Each thread:
- Reads the current value
- Calculates the new value
- Uses CAS to update it atomically
- Retries if another thread modified the value first

The final counter value will be **12** (3 increments × 4 threads), demonstrating that all updates were successful despite concurrent access.

## Example Output

```
Thread 0 updated counter: 0 -> 1
Thread 1 updated counter: 1 -> 2
Thread 2 updated counter: 2 -> 3
Thread 0 in non-critical section
Thread 3 updated counter: 3 -> 4
...
Final counter value: 12
Final data value: Updated by Thread X
```

## Key Features

✅ **Lock-Free**: No locks needed, threads don't block  
✅ **Atomic Operations**: All updates happen atomically  
✅ **Optimistic Concurrency**: Assumes no conflict, retries if needed  
✅ **Works for N threads**: Supports any number of concurrent threads  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` classes

## Implementation Details

This implementation uses modern Java concurrency features:

### **Atomic Variables**
- **`AtomicInteger`**: For the shared counter
  - `get()`: Reads the current value
  - `compareAndSet(expected, update)`: Performs the CAS operation
  - Returns `true` if the value was updated, `false` if it changed

- **`AtomicReference<String>`**: For the shared data object
  - Same CAS semantics as `AtomicInteger`
  - Can hold any object reference

### **CAS Retry Loop Pattern**
```java
boolean success = false;
while (!success) {
    int currentValue = sharedCounter.get();
    int newValue = currentValue + 1;
    success = sharedCounter.compareAndSet(currentValue, newValue);
    if (!success) {
        Thread.onSpinWait(); // Optimize CPU usage while waiting
    }
}
```

### **Virtual Threads** (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Perfect for high-concurrency scenarios

### **Optimized Spin-Wait** (`Thread.onSpinWait()`)
- Hints to the CPU for better performance during busy-waiting
- Improves CPU efficiency when waiting for CAS to succeed
- Available since Java 9

## Real-World Analogy

Think of updating a shared whiteboard:
- **Current value**: What's written on the whiteboard now
- **Expected value**: What you think is on the whiteboard
- **New value**: What you want to write
- **CAS operation**: You check if the whiteboard still says what you expect, and only then do you write your new value
  - If someone else changed it: You read the new value and try again
  - If it's still what you expected: You write your update successfully

## CAS vs. Traditional Locks

| Feature | Traditional Locks | CAS |
|---------|------------------|-----|
| Blocking | Threads block waiting for lock | Threads retry, don't block |
| Performance | Can cause context switching | More efficient, no blocking |
| Deadlocks | Possible | Not possible (no locks) |
| Complexity | Simpler mental model | Requires retry logic |
| Use Case | Long critical sections | Short, frequent updates |

## Advantages of CAS

1. **No Deadlocks**: Since there are no locks, deadlocks cannot occur
2. **Better Performance**: No thread blocking means less context switching
3. **Scalability**: Works well with many threads
4. **Lock-Free**: Threads make progress independently

## Limitations

⚠️ **ABA Problem**: CAS can be tricked if a value changes from A → B → A between reads. The current implementation is simple and doesn't handle this, but for most use cases (like counters), this isn't a problem.

⚠️ **Retry Loops**: If many threads compete, some may need to retry many times before succeeding. However, this is usually faster than blocking with locks.

## Common Use Cases

- **Counters**: Incrementing/decrementing shared counters
- **Stack/Queue Operations**: Lock-free data structures
- **Reference Updates**: Updating shared object references
- **State Machines**: Transitioning between states atomically

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for atomic operations

---

**Note**: This implementation demonstrates the Compare-and-Swap algorithm using modern Java concurrency utilities. CAS is the foundation for many lock-free algorithms and data structures. While the algorithm itself is conceptually simple, it enables powerful concurrent programming patterns without the overhead and complexity of traditional locking mechanisms.

