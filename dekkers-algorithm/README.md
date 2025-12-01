# Dekker's Algorithm

## What is Dekker's Algorithm?

Dekker's Algorithm is a **classic software-only solution for mutual exclusion** between exactly two processes, developed by Dutch mathematician Th. J. Dekker in 1965. It was one of the first algorithms to solve the critical section problem using only software, without requiring special hardware instructions.

## The Problem It Solves

The algorithm solves the **mutual exclusion problem** for two processes:
- Ensures only one process can be in its critical section at a time
- Prevents race conditions when accessing shared resources
- Works without hardware support (pure software solution)
- Guarantees progress (no deadlock, no starvation)

## How It Works

Dekker's Algorithm uses three shared variables:
1. **flag0** - Boolean flag indicating process 0 wants to enter critical section
2. **flag1** - Boolean flag indicating process 1 wants to enter critical section
3. **turn** - Integer indicating whose turn it is (0 or 1)

### Algorithm Steps

#### Enter Critical Section

```java
1. Set own flag to true (indicate desire to enter)
2. While other process's flag is true:
   a. If it's not our turn:
      - Set our flag to false
      - Wait until it's our turn
      - Set our flag back to true
   b. Otherwise, continue waiting
3. Enter critical section
```

#### Exit Critical Section

```java
1. Set own flag to false
2. Give turn to the other process
```

## Key Properties

- **Mutual Exclusion**: Only one process can be in critical section at a time
- **Progress**: If no process is in critical section, some process will eventually enter
- **Bounded Waiting**: No process waits indefinitely
- **Software-Only**: No special hardware instructions required

## Comparison with Peterson's Algorithm

| Feature | Dekker's | Peterson's |
|---------|----------|------------|
| Complexity | More complex logic | Simpler logic |
| Turn handling | Explicit turn checking | Turn set before waiting |
| Flag management | Conditional flag setting | Always set flag before turn |
| Readability | More complex | More intuitive |

Both algorithms solve the same problem but with different approaches. Peterson's Algorithm is generally considered more elegant and easier to understand.

## Limitations

1. **Only 2 Processes**: Works only for exactly two processes (cannot be extended to N processes)
2. **Busy Waiting**: Uses spin-waiting, wasting CPU cycles
3. **Not Practical**: Modern systems use hardware-supported atomic operations

## Use Cases

- **Educational**: Understanding mutual exclusion principles
- **Historical**: Studying the evolution of concurrency algorithms
- **Embedded Systems**: Systems without hardware atomic operations (rare)
- **Algorithm Design**: Foundation for understanding more complex algorithms

## Implementation Details

The implementation uses:
- `AtomicBoolean` for flags (thread-safe boolean operations)
- `AtomicInteger` for turn variable (thread-safe integer operations)
- `Thread.onSpinWait()` for efficient busy-waiting (Java 9+)

## Running the Code

```bash
cd dekkers-algorithm
javac DekkersAlgorithm.java
java DekkersAlgorithm
```

## Expected Output

The program demonstrates:
- Two threads accessing a shared counter
- Mutual exclusion ensuring correct counter increments
- No race conditions or lost updates
- Final counter value equals expected value (2 × iterations)

## Complexity

- **Time Complexity**: O(1) per critical section entry (amortized)
- **Space Complexity**: O(1) - constant space for flags and turn variable
- **Scalability**: ❌ Only works for 2 threads

## References

- Original algorithm by Th. J. Dekker (1965)
- Foundation for many subsequent mutual exclusion algorithms
- Historical significance in concurrent programming

