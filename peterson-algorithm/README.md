# Peterson's Algorithm

## What is Peterson's Algorithm?

Peterson's Algorithm is a classic solution to the **mutual exclusion problem** in concurrent programming. It ensures that when two threads (or processes) need to access a shared resource, only one thread can access it at a time.

## The Problem It Solves

Imagine two threads trying to increment the same counter:
- **Without protection**: Both threads might read the same value, increment it, and write it back, causing lost updates
- **With Peterson's Algorithm**: Only one thread can modify the counter at a time, ensuring correctness

## How It Works

Peterson's Algorithm uses three simple ideas:

### 1. **Flags** (`flag0` and `flag1`)
   - Each thread raises its "flag" when it wants to enter the critical section
   - Think of it like raising your hand to say "I want to go!"

### 2. **Turn Variable** (`turn`)
   - Indicates whose turn it is
   - Like taking turns in a game - if both want to go, the turn variable decides who goes first

### 3. **The Waiting Loop**
   - A thread waits if:
     - The other thread's flag is raised (they want to go too) **AND**
     - It's the other thread's turn
   - Once the other thread finishes, it lowers its flag, and the waiting thread can proceed

## The Algorithm Steps

When a thread wants to enter the critical section:

1. **Raise your flag**: Set `flag[id] = true` (I want to go!)
2. **Give the turn to the other**: Set `turn = other` (be polite, let them go first if they're waiting)
3. **Wait if needed**: While the other thread's flag is up AND it's their turn, wait
4. **Enter critical section**: Do your work safely
5. **Lower your flag**: Set `flag[id] = false` when done (I'm finished!)

## Running the Program

```bash
cd peterson-algorithm
javac PetersonsAlgorithm.java
java PetersonsAlgorithm
```

## What You'll See

The program creates two threads that each try to increment a shared counter 5 times:
- Each thread alternates between critical and non-critical sections
- Only one thread can be in the critical section at a time
- The final counter value will be **10** (5 increments × 2 threads)
- The output shows which thread is in the critical section and the current counter value

## Example Output

```
Thread 0 in critical section, counter: 1
Thread 1 in critical section, counter: 2
Thread 0 in non-critical section
Thread 1 in non-critical section
...
Final counter value: 10
```

## Key Features

✅ **Mutual Exclusion**: Only one thread in critical section at a time  
✅ **Progress**: If a thread wants to enter, it will eventually get in  
✅ **Bounded Waiting**: No thread waits forever  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` classes

## Implementation Details

This implementation uses modern Java concurrency features:

- **Atomic Variables** (`AtomicBoolean`, `AtomicInteger`): Thread-safe operations without explicit locks
  - `AtomicBoolean` for flags and `AtomicInteger` for turn and counter
  - Atomic operations guarantee visibility and atomicity across threads

- **Virtual Threads** (`Thread.ofVirtual()`): Lightweight threads introduced in Java 21
  - Can create millions of virtual threads efficiently
  - Managed by the JVM, not the operating system

- **Optimized Spin-Wait** (`Thread.onSpinWait()`): Hints to the CPU for better performance
  - Improves CPU efficiency during busy-waiting loops
  - Available since Java 9

## Real-World Analogy

Think of a single-occupancy bathroom:
- **Flags**: The "occupied" signs on each door
- **Turn**: A coin flip to decide who goes first if both arrive at the same time
- **Waiting**: You wait outside if the other person is inside and it's their turn
- **Critical Section**: The bathroom itself (only one person at a time!)

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

---

**Note**: This implementation demonstrates Peterson's Algorithm using modern Java concurrency utilities (`java.util.concurrent.atomic`). While the algorithm itself is a classic software-only solution, this implementation leverages atomic operations for better performance and thread safety in practice.

