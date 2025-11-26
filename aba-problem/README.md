# ABA Problem

## What is the ABA Problem?

The ABA problem is a classic issue in lock-free concurrent programming that occurs when using Compare-and-Swap (CAS) operations. It happens when a value changes from **A** to **B** and back to **A** between when a thread reads it and when it attempts to update it, causing the CAS operation to succeed even though the value was modified in between.

## The Problem It Demonstrates

When using CAS operations for lock-free algorithms:
- **Expected behavior**: CAS should fail if the value changed, even if it changed back to the original value
- **ABA Problem**: CAS succeeds because it only checks if the value matches, not whether it was modified in between
- **Consequence**: This can lead to incorrect behavior in lock-free data structures and algorithms

## How It Works

The ABA problem occurs in this sequence:

1. **Thread 1** reads value **A** and stores it as the expected value
2. **Thread 2** changes the value from **A** → **B** → **A** (the ABA sequence)
3. **Thread 1** attempts CAS with expected value **A** and new value **C**
4. **CAS succeeds** because the current value is **A**, even though it's not the same **A** that Thread 1 originally read

### The Critical Issue

The problem is that CAS operations typically use `equals()` to compare values, which may return `true` for different instances that represent the same logical value. This means:
- The value appears unchanged (A → A)
- But the underlying state or history may have changed
- CAS incorrectly succeeds, potentially causing data corruption

## Running the Program

```bash
cd aba-problem
javac *.java
java ABADemo
```

## What You'll See

The program demonstrates the ABA problem with three threads:

1. **Reader Thread 0**: Reads value A, does some work, then attempts to update to C
2. **ABA Thread 1**: Changes value from A → B → A while other threads are working
3. **Reader Thread 2**: Reads value A, does some work, then attempts to update to C

The output shows:
- Initial value: A (v1)
- Threads reading the value
- The ABA change happening (A → B → A)
- CAS operations succeeding even though the value was modified

### Example Output

```
=== ABA Problem Demonstration ===
Initial value: A (v1)

Thread 0 reads: A (v1)
Thread 1 performs A->B->A change
Thread 2 reads: A (v3)
Thread 0 CAS succeeded!
Thread 2 CAS succeeded!
Final value: C (v11)
```

Notice that Thread 0's CAS succeeded even though the value changed from A (v1) to A (v3) in between!

## Key Features

✅ **Demonstrates ABA Problem**: Shows how CAS can be tricked by A→B→A changes  
✅ **Version Tracking**: Uses version numbers to show the value changed even though it looks the same  
✅ **Lock-Free**: Uses atomic operations without traditional locks  
✅ **Modern Java Implementation**: Uses `java.util.concurrent.atomic` and virtual threads  
✅ **Clear Demonstration**: Easy to understand the problem through code and output

## Implementation Details

This implementation uses modern Java concurrency features:

### **AtomicReference**
- **`AtomicReference<ValueWrapper>`**: Holds a wrapper object with value and version
  - `get()`: Reads the current value
  - `compareAndSet(expected, update)`: Performs the CAS operation
  - `set(newValue)`: Directly sets the value (used for ABA demonstration)

### **ValueWrapper Class**
- Contains both a `value` (String) and `version` (int)
- `equals()` method only compares the `value`, not the `version`
- This is intentional to demonstrate the ABA problem
- The version number shows that the value changed even though it looks the same

### **Virtual Threads** (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Perfect for demonstrating concurrent scenarios
- Managed by the JVM efficiently

### **The ABA Sequence**
```java
public void performABAChange() {
    ValueWrapper current = sharedValue.get();
    sharedValue.set(new ValueWrapper("B", current.version + 1));  // A → B
    sharedValue.set(new ValueWrapper("A", current.version + 2));  // B → A
}
```

## Real-World Analogy

Think of a shared bank account balance:
- **Thread 1** reads balance: $100
- **Thread 2** withdraws $50 (balance: $50), then deposits $50 (balance: $100)
- **Thread 1** checks if balance is still $100 and performs an operation
- The balance looks the same ($100), but transactions happened in between!

## Why It's a Problem

The ABA problem can cause serious issues in lock-free data structures:

1. **Memory Reuse**: In lock-free linked lists, a node might be freed and reused
   - Thread reads pointer to node A
   - Node A is freed, new node A is allocated at the same address
   - CAS succeeds, but points to a completely different node

2. **State Corruption**: The logical state may have changed even if the value looks the same
   - A stack might have items pushed and popped
   - The top pointer looks the same, but the stack contents changed

3. **Lost Updates**: Updates might be applied to stale state
   - Operations based on old state are applied to new state
   - Can lead to data corruption or incorrect results

## Solutions to ABA Problem

Common solutions include:

1. **Version Numbers/Stamps**: Add a version counter that increments on each change
   - Use `AtomicStampedReference` in Java
   - CAS checks both value and version

2. **Hazard Pointers**: Track which memory locations are in use
   - Prevent memory from being reused while threads are accessing it

3. **GC-Safe Languages**: Languages with garbage collection (like Java) help
   - Objects aren't freed while references exist
   - But ABA can still occur with value-based comparisons

## Java's AtomicStampedReference

Java provides `AtomicStampedReference` to solve the ABA problem:

```java
AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
int[] stampHolder = new int[1];
String current = ref.get(stampHolder);
int currentStamp = stampHolder[0];
ref.compareAndSet(current, "C", currentStamp, currentStamp + 1);
```

This ensures CAS only succeeds if both the value AND the stamp match.

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent.atomic` package

---

**Note**: This implementation demonstrates the ABA problem using a simple, clear example. In real-world scenarios, the ABA problem can be more subtle and cause serious bugs in lock-free data structures. Understanding this problem is crucial for writing correct concurrent code.

