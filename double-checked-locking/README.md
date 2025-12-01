# Double-Checked Locking Pattern

## What is Double-Checked Locking?

**Double-Checked Locking** is a software design pattern used to reduce the overhead of acquiring a lock by first testing the locking criterion without actually acquiring the lock. Only if the check indicates that locking is required does the actual locking proceed.

The pattern is particularly useful for **lazy initialization** of expensive objects in multithreaded environments, such as singletons.

## The Problem It Solves

### Problem 1: Expensive Synchronization

```java
// Slow: Every access requires synchronization
public synchronized Singleton getInstance() {
    if (instance == null) {
        instance = new Singleton();
    }
    return instance;
}
```

### Problem 2: Race Condition Without Synchronization

```java
// Fast but broken: Race condition
public Singleton getInstance() {
    if (instance == null) {  // Thread 1 checks: null
                             // Thread 2 checks: null (before Thread 1 creates)
        instance = new Singleton();  // Both create instances!
    }
    return instance;
}
```

## How It Works

The pattern uses **two checks**:
1. **First check** (without lock): Fast path for already-initialized case
2. **Second check** (with lock): Ensures only one thread creates the instance

### Basic Pattern

```java
public Singleton getInstance() {
    if (instance == null) {              // First check (no lock)
        synchronized (this) {
            if (instance == null) {      // Second check (with lock)
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

## Implementation Variants

### 1. Traditional (with synchronized)

```java
private static Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

**Note**: In older Java versions, this could have visibility issues. Modern JVMs handle this correctly, but using `volatile` is recommended.

### 2. With volatile (Recommended)

```java
private static volatile Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

The `volatile` keyword ensures proper visibility of the `instance` variable across threads.

### 3. With AtomicReference (Lock-free)

```java
private static final AtomicReference<Singleton> instance = 
    new AtomicReference<>(null);

public static Singleton getInstance() {
    Singleton current = instance.get();
    if (current == null) {
        Singleton newInstance = new Singleton();
        if (instance.compareAndSet(null, newInstance)) {
            return newInstance;
        } else {
            return instance.get();
        }
    }
    return current;
}
```

## Key Properties

- **Performance**: Reduces synchronization overhead after initialization
- **Correctness**: Ensures only one instance is created
- **Thread-Safe**: Prevents race conditions
- **Lazy**: Object created only when needed

## Use Cases

### 1. Singleton Pattern

Most common use case - ensuring only one instance exists.

### 2. Lazy Initialization

Initializing expensive resources only when needed:

```java
private volatile ExpensiveResource resource;

public ExpensiveResource getResource() {
    ExpensiveResource result = resource;
    if (result == null) {
        synchronized (this) {
            result = resource;
            if (result == null) {
                resource = result = createResource();
            }
        }
    }
    return result;
}
```

### 3. Caching

Lazy loading of cached data.

### 4. Resource Management

Initializing shared resources on first access.

## Important Considerations

### The Java Memory Model

In older Java versions (before Java 5), double-checked locking had issues due to:
- **Out-of-order writes**: Object reference might be visible before initialization completes
- **Visibility problems**: Changes might not be visible to other threads

**Solution**: Use `volatile` keyword (Java 5+) or `AtomicReference`.

### Modern Alternatives

1. **Initialization-on-demand holder idiom**:
```java
private static class Holder {
    static final Singleton INSTANCE = new Singleton();
}

public static Singleton getInstance() {
    return Holder.INSTANCE;
}
```

2. **Enum Singleton**:
```java
public enum Singleton {
    INSTANCE;
}
```

## Complexity

- **Time Complexity**: O(1) - constant time after initialization
- **Space Complexity**: O(1) - single instance
- **Synchronization Overhead**: Minimal after first initialization

## Advantages

✅ **Performance**: Fast access after initialization  
✅ **Lazy Loading**: Object created only when needed  
✅ **Thread-Safe**: Prevents multiple instantiations  
✅ **Memory Efficient**: Only one instance exists  

## Limitations

⚠️ **Complexity**: More complex than simple synchronization  
⚠️ **Historical Issues**: Had problems in older Java versions  
⚠️ **Modern Alternatives**: Simpler patterns exist (enum, holder)  

## Running the Code

```bash
cd double-checked-locking
javac DoubleCheckedLocking.java
java DoubleCheckedLocking
```

## Expected Output

The program demonstrates:
- Multiple threads accessing singleton
- Only one instance created regardless of thread count
- Different implementation approaches
- Correctness verification

## Best Practices

1. **Use `volatile`** for the instance variable (Java 5+)
2. **Prefer simpler alternatives** (enum, holder) when possible
3. **Document the pattern** if you must use it
4. **Consider AtomicReference** for lock-free approach

## References

- Design pattern for lazy initialization
- Historical significance in Java concurrency
- Modern alternatives and best practices
- Java Memory Model considerations

