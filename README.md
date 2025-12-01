# Concurrency Algorithms

This repository contains implementations of various concurrent algorithms in Java, utilizing Java 21 features and the `java.util.concurrent` package.

## Overview

This project demonstrates different concurrency control mechanisms and lock-free data structures, implemented using modern Java 21 features and the Java concurrency package. Each algorithm is implemented as a standalone module with its own documentation.

## Algorithms Included

- **[Bakery Algorithm](bakery-algorithm/)** - A lock-free algorithm for mutual exclusion that works for N processes. It uses a ticket-based system where threads take a number and enter the critical section in order, ensuring fairness and preventing starvation.

- **[CLH Lock](clh-lock/)** - Craig, Landin, and Hagersten queue-based spinlock that provides fair, efficient mutual exclusion. Threads form a queue and each thread spins on its predecessor's node, reducing cache contention and improving scalability.

- **[ABA Problem](aba-problem/)** - A classic issue in lock-free concurrent programming that occurs when a value changes from A to B and back to A between when a thread reads it and when it attempts to update it. This causes CAS operations to succeed even though the value was modified in between, potentially leading to incorrect behavior in lock-free data structures.

- **[Compare-and-Swap (CAS)](compare-and-swap/)** - A fundamental atomic operation for lock-free synchronization. It atomically updates a value only if it matches an expected value, enabling lock-free data structures and algorithms without traditional blocking mechanisms.

- **[Dekker's Algorithm](dekkers-algorithm/)** - A classic software-only solution for mutual exclusion between exactly two processes, developed by Dutch mathematician Th. J. Dekker. Uses flags and a turn variable to coordinate access to shared resources without hardware support, similar to Peterson's Algorithm but with a different approach.

- **[Double-Checked Locking](double-checked-locking/)** - A design pattern used to reduce the overhead of acquiring a lock by first testing the locking criterion without actually acquiring the lock. Particularly useful for lazy initialization of singletons and expensive-to-create objects in multithreaded environments.

- **[Exponential Backoff](exponential-backoff/)** - A retry strategy that handles resource contention by waiting with exponentially increasing delays between retry attempts. This reduces system load and gracefully manages high contention scenarios.

- **[Fetch-and-Add](fetch-and-add/)** - A fundamental atomic operation that atomically reads a value from memory, adds a constant to it, and writes the result back, returning the original value. Essential for implementing counters, accumulators, and other concurrent data structures.

- **[Harris Linked List](harris-linked-list/)** - A lock-free concurrent linked list implementation that allows multiple threads to insert, delete, and search elements simultaneously. Uses a two-phase deletion strategy (mark then unlink) with atomic compare-and-swap operations to maintain thread safety without locks.

- **[Lock-Free Hash Table](lock-free-hash-table/)** - A lock-free concurrent hash table implementation that allows multiple threads to insert, retrieve, and remove key-value pairs simultaneously. Uses bucket-based design with lock-free linked lists and atomic compare-and-swap operations to maintain thread safety without locks.

- **[Lock-Free Skip List](lock-free-skip-list/)** - A lock-free concurrent skip list implementation that provides efficient sorted data structure operations. Uses multiple levels of linked lists with probabilistic balancing, allowing O(log n) average-case search, insert, and delete operations. Implements lock-free algorithms using atomic compare-and-swap operations for thread-safe concurrent access.

- **[MCS Lock](mcs-lock/)** - Mellor-Crummey and Scott queue-based spinlock that provides fair mutual exclusion. Unlike CLH, threads spin on their own local variable, making it particularly effective for uniform memory access architectures.

- **[Michael-Scott Queue](michael-scott-queue/)** - A lock-free concurrent queue implementation that allows multiple threads to enqueue and dequeue elements simultaneously. Uses atomic compare-and-swap operations to maintain thread safety without locks.

- **[Peterson's Algorithm](peterson-algorithm/)** - A classic software-only solution for mutual exclusion between exactly two processes. Uses flags and a turn variable to coordinate access to shared resources without hardware support.

- **[Test-and-Set Lock](test-and-set-lock/)** - A simple locking mechanism that uses an atomic test-and-set operation to acquire exclusive access. Threads spin-wait until the lock becomes available, providing mutual exclusion for any number of threads.

- **[Treiber Stack](treiber-stack/)** - A lock-free stack data structure that provides thread-safe push and pop operations using atomic compare-and-swap. One of the simplest and most elegant lock-free data structures, serving as a fundamental building block in concurrent programming.

- **[Work-Stealing Queue](work-stealing-queue/)** - A lock-free concurrent queue optimized for producer-consumer scenarios where the producer and consumer are often the same thread. Each thread has its own queue, and when a thread's queue is empty, it can "steal" work from other threads' queues. Fundamental to work-stealing schedulers used in parallel computing frameworks.

- **[Elimination Backoff Stack](elimination-backoff-stack/)** - An optimized lock-free stack that uses an "elimination array" to allow concurrent push and pop operations to cancel each other out without accessing the main stack. This reduces contention and improves scalability significantly.

- **[Epoch-Based Reclamation](epoch-based-reclamation/)** - A memory reclamation technique for lock-free data structures. Threads operate in epochs, and memory can only be reclaimed when no threads are in older epochs. More efficient than hazard pointers for high-throughput scenarios.

- **[Flat Combining](flat-combining/)** - A synchronization technique where one thread (the combiner) executes operations on behalf of other threads, reducing contention and improving cache locality. Threads publish operations to a shared queue, and the combiner processes them in batch.

- **[Hazard Pointers](hazard-pointers/)** - A memory reclamation technique for lock-free data structures. Each thread maintains a set of "hazard pointers" that protect nodes from being reclaimed while they're being accessed, allowing safe memory reclamation.

- **[Lock-Free Counter](lock-free-counter/)** - A high-performance lock-free counter implementation that supports multiple concurrent increment and decrement operations. Uses atomic operations to maintain thread safety without locks.

- **[Lock-Free Deque](lock-free-deque/)** - A lock-free concurrent double-ended queue that allows operations on both ends (push/pop from left and right). Uses a doubly-linked list with atomic operations to maintain thread safety.

- **[Lock-Free Priority Queue](lock-free-priority-queue/)** - A lock-free concurrent priority queue implementation using a sorted linked list. Elements are maintained in priority order, with the highest priority element at the head.

- **[Lock-Free Reader-Writer Lock](lock-free-reader-writer-lock/)** - A reader-writer lock implementation using atomic operations. Allows multiple readers or a single writer, but uses lock-free mechanisms instead of traditional blocking locks.

- **[Lock-Free Ring Buffer](lock-free-ring-buffer/)** - A bounded lock-free concurrent ring buffer that supports multiple producers and consumers. Uses atomic operations to maintain thread safety and provides backpressure when full.

- **[Lock-Free Set](lock-free-set/)** - A lock-free concurrent set implementation that maintains a collection of unique elements. Uses a sorted linked list structure with atomic operations to maintain thread safety.

- **[Lock-Free Vector](lock-free-vector/)** - A lock-free concurrent resizable array that supports dynamic growth while maintaining thread safety through lock-free operations. Allows multiple threads to read and write elements concurrently.

- **[Seqlock](seqlock/)** - A synchronization mechanism optimized for read-heavy workloads. Allows multiple concurrent readers without blocking, while writers use a sequence number to indicate updates. Readers check the sequence number to detect concurrent writes.

- **[Timeout Lock](timeout-lock/)** - A lock implementation that supports timeout for lock acquisition. Threads can attempt to acquire the lock with a maximum wait time, and if the lock cannot be acquired within that time, the operation fails instead of blocking indefinitely.

- **[Wait-Free Queue](wait-free-queue/)** - A wait-free concurrent queue where every operation completes in a bounded number of steps, regardless of other threads' behavior. Uses helping mechanisms where threads help other threads complete their operations.

## Algorithm Comparison

| Algorithm | Complexity | Lock-Free | Wait-Free | Scalable | Difficulty |
|-----------|------------|-----------|-----------|----------|------------|
| **ABA Problem** | O(1) time, O(1) space | ✅ Yes | ❌ No | N/A (Demonstration) | 🟢 Easy |
| **Bakery Algorithm** | O(n) time, O(n) space | ✅ Yes | ❌ No | ⚠️ Moderate | 🟡 Medium |
| **CLH Lock** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Compare-and-Swap (CAS)** | O(1) amortized, O(1) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Dekker's Algorithm** | O(1) time, O(1) space | ✅ Yes | ❌ No | ❌ No (2 threads only) | 🟢 Easy |
| **Double-Checked Locking** | O(1) time, O(1) space | ❌ No | ❌ No | ⚠️ Moderate | 🟢 Easy |
| **Exponential Backoff** | O(1) per retry, O(1) space | N/A (Strategy) | ❌ No | ⚠️ Moderate | 🟢 Easy |
| **Fetch-and-Add** | O(1) time, O(1) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Harris Linked List** | O(n) worst case, O(1) avg, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Hash Table** | O(1) avg, O(n) worst case, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Skip List** | O(log n) avg, O(n) worst case, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🔴 Hard |
| **MCS Lock** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Michael-Scott Queue** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Peterson's Algorithm** | O(1) time, O(1) space | ✅ Yes | ❌ No | ❌ No (2 threads only) | 🟢 Easy |
| **Test-and-Set Lock** | O(1) time, O(1) space | ✅ Yes | ❌ No | ❌ Poor | 🟢 Easy |
| **Treiber Stack** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Work-Stealing Queue** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Elimination Backoff Stack** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Epoch-Based Reclamation** | O(1) time, O(e) space | N/A (Technique) | ❌ No | ✅ Excellent | 🟡 Medium |
| **Flat Combining** | O(1) avg, O(k) worst, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Hazard Pointers** | O(1) time, O(k×h) space | N/A (Technique) | ❌ No | ⚠️ Moderate | 🟡 Medium |
| **Lock-Free Counter** | O(1) time, O(1) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Lock-Free Deque** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Priority Queue** | O(n) worst, O(n) avg, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Reader-Writer Lock** | O(1) time, O(1) space | ✅ Yes | ❌ No | ⚠️ Moderate | 🟡 Medium |
| **Lock-Free Ring Buffer** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Set** | O(n) worst, O(n) avg, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Vector** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Seqlock** | O(1) avg, O(k) worst, O(1) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Timeout Lock** | O(1) avg, O(timeout) worst, O(1) space | ✅ Yes | ❌ No | ⚠️ Moderate | 🟢 Easy |
| **Wait-Free Queue** | O(k) bounded, O(n) space | ✅ Yes | ✅ Yes | ✅ Excellent | 🔴 Hard |

### Legend
- **Complexity**: Time and space complexity of operations
- **Lock-Free**: Algorithm doesn't use traditional locks (mutexes/semaphores)
- **Wait-Free**: Every thread makes progress in a bounded number of steps (stronger than lock-free)
- **Scalable**: Performance characteristics with increasing thread count
- **Difficulty**: Implementation complexity (Easy/Medium/Hard)

## Requirements

- Java 21 or higher
- Java Concurrency Package (`java.util.concurrent`)

## Usage

Each algorithm is located in its own directory with a README file explaining its implementation and usage. Navigate to the specific algorithm directory for detailed documentation.

### Quick Start

1. Navigate to any algorithm directory
2. Compile the Java file: `javac <AlgorithmName>.java`
3. Run the program: `java <AlgorithmName>`

Each implementation includes a `main` method with example usage demonstrating concurrent operations using virtual threads.