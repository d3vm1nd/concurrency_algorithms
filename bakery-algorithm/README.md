# Bakery Algorithm

## What is the Bakery Algorithm?

The Bakery Algorithm is a solution to the **mutual exclusion problem** that works for **N processes** (not just 2). It's called "Bakery" because it mimics a real bakery where customers take a number and wait for their turn to be served.

## The Problem It Solves

When multiple threads need to access a shared resource:
- **Without protection**: Threads might interfere with each other, causing data corruption
- **With Bakery Algorithm**: Threads take a "ticket number" and enter the critical section in order, ensuring only one thread accesses the resource at a time

## How It Works

The Bakery Algorithm uses two key concepts:

### 1. **Choosing Array** (`choosing[]`)
   - Indicates when a thread is in the process of selecting a ticket number
   - Prevents race conditions while getting a number

### 2. **Number Array** (`number[]`)
   - Stores the "ticket number" for each thread
   - Threads with lower numbers (or same number but lower ID) enter first
   - A number of 0 means the thread is not interested in entering

## The Algorithm Steps

When a thread wants to enter the critical section:

1. **Start choosing**: Set `choosing[id] = true` (I'm picking a number!)
2. **Get a ticket**: Find the maximum number currently in use, add 1, and assign it to yourself
3. **Stop choosing**: Set `choosing[id] = false` (I have my number!)
4. **Wait for your turn**: For each other thread:
   - Wait if they're still choosing a number
   - Wait if they have a lower number, or the same number but a lower ID
5. **Enter critical section**: Do your work safely
6. **Return your ticket**: Set `number[id] = 0` when done (I'm finished!)

## Why It Works

- **Mutual Exclusion**: Only one thread can be in the critical section because they enter in order
- **Fairness**: Threads enter in the order they got their numbers (FIFO-like)
- **No Starvation**: Every thread will eventually get a turn
- **Works for N threads**: Unlike Peterson's Algorithm (which works for 2), this works for any number of threads

## Running the Program

```bash
cd bakery-algorithm
javac BakeryAlgorithm.java
java BakeryAlgorithm
```

## What You'll See

The program creates 4 threads that each try to increment a shared counter 3 times:
- Each thread gets a ticket number when it wants to enter
- Threads enter the critical section in order of their ticket numbers
- The final counter value will be **12** (3 increments × 4 threads)
- The output shows which thread is in the critical section and the current counter value

## Example Output

```
Thread 0 in critical section, counter: 1
Thread 1 in critical section, counter: 2
Thread 2 in critical section, counter: 3
Thread 3 in critical section, counter: 4
...
Final counter value: 12
```

## Key Features

✅ **Mutual Exclusion**: Only one thread in critical section at a time  
✅ **Progress**: If a thread wants to enter, it will eventually get in  
✅ **Bounded Waiting**: No thread waits forever  
✅ **Works for N threads**: Not limited to just 2 processes  
✅ **Fair Ordering**: Threads enter in the order they request access  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` classes

## Implementation Details

This implementation uses modern Java concurrency features:

- **Atomic Variables** (`AtomicBoolean[]`, `AtomicIntegerArray`, `AtomicInteger`): Thread-safe operations without explicit locks
  - `AtomicBoolean[]` for choosing flags
  - `AtomicIntegerArray` for ticket numbers
  - `AtomicInteger` for the shared counter
  - Atomic operations guarantee visibility and atomicity across threads

- **Virtual Threads** (`Thread.ofVirtual()`): Lightweight threads introduced in Java 21
  - Can create millions of virtual threads efficiently
  - Managed by the JVM, not the operating system
  - Perfect for high-concurrency scenarios

- **Optimized Spin-Wait** (`Thread.onSpinWait()`): Hints to the CPU for better performance
  - Improves CPU efficiency during busy-waiting loops
  - Available since Java 9

## Real-World Analogy

Think of a bakery with a ticket dispenser:
- **Choosing**: You're at the machine, pressing the button to get a ticket
- **Number**: Your ticket number (e.g., "Now serving: 42")
- **Waiting**: You wait until your number is called
- **Critical Section**: The counter where you place your order (only one customer at a time!)
- **Returning ticket**: You're done, so you discard your ticket

## Comparison with Peterson's Algorithm

| Feature | Peterson's | Bakery |
|---------|-----------|--------|
| Number of processes | 2 only | N processes |
| Complexity | Simpler | More complex |
| Fairness | Basic | FIFO ordering |
| Memory usage | Less | More (arrays) |

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package for thread-safe operations

---

**Note**: This implementation demonstrates the Bakery Algorithm using modern Java concurrency utilities (`java.util.concurrent.atomic`). While the algorithm itself is a classic software-only solution, this implementation leverages atomic operations and virtual threads for better performance and scalability in practice.

