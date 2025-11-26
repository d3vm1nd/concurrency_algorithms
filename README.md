# Concurrency Algorithms

This repository contains implementations of various concurrent algorithms in Java, utilizing Java 21 features and the `java.util.concurrent` package.

## Overview

This project demonstrates different concurrency control mechanisms and lock-free data structures, implemented using modern Java 21 features and the Java concurrency package. Each algorithm is implemented as a standalone module with its own documentation.

## Algorithms Included

- **[Bakery Algorithm](bakery-algorithm/)** - A lock-free algorithm for mutual exclusion that works for N processes. It uses a ticket-based system where threads take a number and enter the critical section in order, ensuring fairness and preventing starvation.

- **[CLH Lock](clh-lock/)** - Craig, Landin, and Hagersten queue-based spinlock that provides fair, efficient mutual exclusion. Threads form a queue and each thread spins on its predecessor's node, reducing cache contention and improving scalability.

- **[Compare-and-Swap (CAS)](compare-and-swap/)** - A fundamental atomic operation for lock-free synchronization. It atomically updates a value only if it matches an expected value, enabling lock-free data structures and algorithms without traditional blocking mechanisms.

- **[Exponential Backoff](exponential-backoff/)** - A retry strategy that handles resource contention by waiting with exponentially increasing delays between retry attempts. This reduces system load and gracefully manages high contention scenarios.

- **[Harris Linked List](harris-linked-list/)** - A lock-free concurrent linked list implementation that allows multiple threads to insert, delete, and search elements simultaneously. Uses a two-phase deletion strategy (mark then unlink) with atomic compare-and-swap operations to maintain thread safety without locks.

- **[Lock-Free Hash Table](lock-free-hash-table/)** - A lock-free concurrent hash table implementation that allows multiple threads to insert, retrieve, and remove key-value pairs simultaneously. Uses bucket-based design with lock-free linked lists and atomic compare-and-swap operations to maintain thread safety without locks.

- **[Lock-Free Skip List](lock-free-skip-list/)** - A lock-free concurrent skip list implementation that provides efficient sorted data structure operations. Uses multiple levels of linked lists with probabilistic balancing, allowing O(log n) average-case search, insert, and delete operations. Implements lock-free algorithms using atomic compare-and-swap operations for thread-safe concurrent access.

- **[MCS Lock](mcs-lock/)** - Mellor-Crummey and Scott queue-based spinlock that provides fair mutual exclusion. Unlike CLH, threads spin on their own local variable, making it particularly effective for uniform memory access architectures.

- **[Michael-Scott Queue](michael-scott-queue/)** - A lock-free concurrent queue implementation that allows multiple threads to enqueue and dequeue elements simultaneously. Uses atomic compare-and-swap operations to maintain thread safety without locks.

- **[Peterson's Algorithm](peterson-algorithm/)** - A classic software-only solution for mutual exclusion between exactly two processes. Uses flags and a turn variable to coordinate access to shared resources without hardware support.

- **[Test-and-Set Lock](test-and-set-lock/)** - A simple locking mechanism that uses an atomic test-and-set operation to acquire exclusive access. Threads spin-wait until the lock becomes available, providing mutual exclusion for any number of threads.

- **[Treiber Stack](treiber-stack/)** - A lock-free stack data structure that provides thread-safe push and pop operations using atomic compare-and-swap. One of the simplest and most elegant lock-free data structures, serving as a fundamental building block in concurrent programming.

## Algorithm Comparison

| Algorithm | Complexity | Lock-Free | Wait-Free | Scalable | Difficulty |
|-----------|------------|-----------|-----------|----------|------------|
| **Bakery Algorithm** | O(n) time, O(n) space | ✅ Yes | ❌ No | ⚠️ Moderate | 🟡 Medium |
| **CLH Lock** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Compare-and-Swap (CAS)** | O(1) amortized, O(1) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |
| **Exponential Backoff** | O(1) per retry, O(1) space | N/A (Strategy) | ❌ No | ⚠️ Moderate | 🟢 Easy |
| **Harris Linked List** | O(n) worst case, O(1) avg, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Hash Table** | O(1) avg, O(n) worst case, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Lock-Free Skip List** | O(log n) avg, O(n) worst case, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🔴 Hard |
| **MCS Lock** | O(1) time, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Michael-Scott Queue** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟡 Medium |
| **Peterson's Algorithm** | O(1) time, O(1) space | ✅ Yes | ❌ No | ❌ No (2 threads only) | 🟢 Easy |
| **Test-and-Set Lock** | O(1) time, O(1) space | ✅ Yes | ❌ No | ❌ Poor | 🟢 Easy |
| **Treiber Stack** | O(1) amortized, O(n) space | ✅ Yes | ❌ No | ✅ Excellent | 🟢 Easy |

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