# Test-and-Set Lock

## What is the Test-and-Set Lock?

The Test-and-Set Lock is a simple and efficient solution to the **mutual exclusion problem** in concurrent programming. It uses an atomic operation that both reads and writes a value in a single, uninterruptible step, ensuring that only one thread can acquire the lock at a time.

## The Problem It Solves

When multiple threads need to access a shared resource:
- **Without protection**: Threads might interfere with each other, causing data corruption and race conditions
- **With Test-and-Set Lock**: Threads use an atomic operation to acquire exclusive access, ensuring only one thread accesses the resource at a time

## How It Works

The Test-and-Set Lock uses a single atomic boolean variable:

### **Lock Variable** (`lock`)
   - A boolean flag that indicates whether the lock is currently held
   - `false` means the lock is available (unlocked)
   - `true` means the lock is held by a thread (locked)

### **The Atomic Operation**
   - The `getAndSet(true)` operation atomically:
     1. Reads the current value of the lock
     2. Sets it to `true`
     3. Returns the old value
   - This happens in a single, uninterruptible step

## The Algorithm Steps

When a thread wants to enter the critical section:

1. **Try to acquire the lock**: Use `getAndSet(true)` to atomically test and set the lock
   - If the old value was `false`: You got the lock! Proceed to step 2
   - If the old value was `true`: Another thread has the lock, go to step 3
2. **Enter critical section**: Do your work safely
3. **Release the lock**: Set `lock = false` when done
4. **If you didn't get the lock**: Spin-wait (keep checking) until the lock becomes available, then try again

## Why It Works

- **Mutual Exclusion**: The atomic `getAndSet()` operation ensures only one thread can successfully change the lock from `false` to `true`
- **Progress**: If a thread wants to enter and the lock is free, it will immediately acquire it
- **Simplicity**: Very simple implementation with just one variable
- **Works for N threads**: Unlike Peterson's Algorithm (which works for 2), this works for any number of threads

## Running the Program

```bash
cd test-and-set-lock
javac TestAndSetLock.java
java TestAndSetLock
```

## What You'll See

The program creates 4 threads that each try to increment a shared counter 3 times:
- Each thread must acquire the lock before entering the critical section
- Only one thread can hold the lock at a time
- The final counter value will be **12** (3 increments × 4 threads)
- The output shows which thread is in the critical section and the current counter value

## Example Output

```
Thread 1 in critical section, counter: 1
Thread 3 in critical section, counter: 2
Thread 2 in critical section, counter: 3
Thread 0 in critical section, counter: 4
Thread 0 in non-critical section
Thread 0 in critical section, counter: 5
...
Final counter value: 12
```

## Key Features

✅ **Mutual Exclusion**: Only one thread in critical section at a time  
✅ **Progress**: If a thread wants to enter, it will eventually get in  
✅ **Simplicity**: Very simple implementation with minimal code  
✅ **Works for N threads**: Not limited to just 2 processes  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` classes

## Implementation Details

This implementation uses modern Java concurrency features:

- **Atomic Variables** (`AtomicBoolean`, `AtomicInteger`): Thread-safe operations without explicit locks
  - `AtomicBoolean` for the lock state
  - `AtomicInteger` for the shared counter
  - The `getAndSet()` method provides the atomic test-and-set operation
  - Atomic operations guarantee visibility and atomicity across threads

- **Virtual Threads** (`Thread.ofVirtual()`): Lightweight threads introduced in Java 21
  - Can create millions of virtual threads efficiently
  - Managed by the JVM, not the operating system
  - Perfect for high-concurrency scenarios

- **Optimized Spin-Wait** (`Thread.onSpinWait()`): Hints to the CPU for better performance
  - Improves CPU efficiency during busy-waiting loops
  - Available since Java 9

## Real-World Analogy

Think of a single key to a room:
- **Lock variable**: The key itself (either available or in use)
- **Test-and-Set**: You try to grab the key from the hook
  - If it's there (`false`), you take it and set it to "in use" (`true`)
  - If someone else has it (`true`), you wait and keep checking
- **Critical Section**: The room (only one person can be inside at a time!)
- **Release**: You return the key to the hook (`false`) when you're done

## Comparison with Other Algorithms

| Feature | Peterson's | Bakery | Test-and-Set |
|---------|-----------|--------|--------------|
| Number of processes | 2 only | N processes | N processes |
| Complexity | Simple | More complex | Very simple |
| Fairness | Basic | FIFO ordering | No fairness guarantee |
| Memory usage | Less | More (arrays) | Minimal (1 variable) |
| Starvation risk | Low | None | Possible |

## Limitations

⚠️ **Starvation**: Unlike the Bakery Algorithm, Test-and-Set Lock doesn't guarantee fairness. A thread might wait indefinitely if other threads keep acquiring the lock first. However, in practice, this is rarely a problem.

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

---

**Note**: This implementation demonstrates the Test-and-Set Lock using modern Java concurrency utilities (`java.util.concurrent.atomic`). The `getAndSet()` method provides the atomic test-and-set operation that is fundamental to this algorithm. While the algorithm itself is simple, this implementation leverages atomic operations and virtual threads for better performance and scalability in practice.

