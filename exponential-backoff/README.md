# Exponential Backoff Algorithm

## What is Exponential Backoff?

Exponential Backoff is a **retry strategy** used in concurrent programming to handle resource contention gracefully. When multiple threads try to access a shared resource that's currently busy, instead of immediately retrying (which wastes CPU cycles) or giving up, threads wait with increasing delays before each retry attempt.

## The Problem It Solves

Imagine multiple threads trying to access a limited resource (like a database connection, a file, or a network endpoint):

- **Without backoff**: Threads might continuously retry, wasting CPU and creating unnecessary load
- **With exponential backoff**: Threads wait progressively longer between retries, reducing contention and system load

## How It Works

The algorithm uses a simple but effective strategy:

### Exponential Delay Pattern

The wait time **doubles** after each failed attempt:
- **1st retry**: Wait 10ms
- **2nd retry**: Wait 20ms  
- **3rd retry**: Wait 40ms
- **4th retry**: Wait 80ms
- **5th retry**: Wait 160ms

The formula is: `delay = initialDelay × 2^retryCount`

### The Algorithm Steps

When a thread wants to access a resource:

1. **Try to acquire**: Attempt to get the resource immediately
2. **If busy**: Calculate exponential delay and wait
3. **Retry**: Try again after waiting
4. **Repeat**: Continue with increasing delays up to max retries
5. **Success or failure**: Either acquire the resource or give up after max retries

## Running the Program

```bash
cd exponential-backoff
javac ExponentialBackoff.java
java ExponentialBackoff
```

## What You'll See

The program creates three virtual threads that each try to access a shared resource:

- Each thread attempts to acquire a semaphore (representing a shared resource)
- If the resource is busy, threads wait with exponential backoff before retrying
- Only one thread can access the resource at a time
- Threads perform work (incrementing a counter) and then release the resource
- The output shows the backoff delays and successful acquisitions

## Example Output

```
Thread 1 waiting 10ms (retry 1)
Thread 2 waiting 10ms (retry 1)
Thread 3 acquired resource, counter: 1
Thread 3 released resource
Thread 1 waiting 20ms (retry 2)
Thread 2 acquired resource, counter: 2
Thread 2 released resource
Thread 1 acquired resource, counter: 3
Thread 1 released resource
Final counter value: 3
```

## Key Features

✅ **Reduces Contention**: Longer waits mean fewer simultaneous retry attempts  
✅ **Efficient Resource Usage**: Avoids CPU spinning and unnecessary load  
✅ **Graceful Degradation**: Handles high contention scenarios elegantly  
✅ **Configurable**: Initial delay and max retries can be adjusted  
✅ **Modern Java Implementation**: Uses `java.util.concurrent` package

## Implementation Details

This implementation uses modern Java concurrency features:

### Semaphore (`java.util.concurrent.Semaphore`)
- Controls access to a shared resource
- `tryAcquire()`: Non-blocking attempt to acquire the resource
- `release()`: Frees the resource for other threads
- Perfect for modeling limited resources

### Virtual Threads (`Thread.ofVirtual()`)
- Lightweight threads introduced in Java 21
- Can create millions of virtual threads efficiently
- Managed by the JVM, not the operating system
- Ideal for I/O-bound operations and high-concurrency scenarios

### Atomic Operations (`java.util.concurrent.atomic.AtomicInteger`)
- Thread-safe counter operations
- No explicit locking required
- Guarantees visibility and atomicity across threads

### Exponential Calculation
- Uses bit-shifting (`1L << retryCount`) for efficient power-of-2 calculation
- Equivalent to `Math.pow(2, retryCount)` but faster
- Prevents overflow with `long` type

## Real-World Applications

Exponential Backoff is widely used in:

- **Network Programming**: Retrying failed HTTP requests or API calls
- **Database Connections**: Handling connection pool exhaustion
- **Distributed Systems**: Coordinating access to shared resources
- **Cloud Services**: AWS, Google Cloud, and Azure use exponential backoff for their APIs
- **Web Browsers**: Handling server overload scenarios
- **Message Queues**: Managing high-throughput message processing

## Algorithm Parameters

- **INITIAL_DELAY_MS**: Starting delay (10ms in this implementation)
- **MAX_RETRIES**: Maximum number of retry attempts (5 in this implementation)
- **Resource**: Semaphore with 1 permit (single-access resource)

## Advantages

1. **Reduces System Load**: Fewer simultaneous retries mean less CPU usage
2. **Handles Burst Traffic**: Gracefully manages sudden spikes in demand
3. **Self-Regulating**: Automatically adapts to contention levels
4. **Simple to Implement**: Easy to understand and maintain

## Considerations

- **Maximum Delay**: Very high retry counts can lead to very long delays
- **Jitter**: Some implementations add random jitter to prevent synchronized retries
- **Timeout**: Consider adding a total timeout to prevent indefinite waiting

## Requirements

- **Java 21+**: Required for virtual threads (`Thread.ofVirtual()`)
- The implementation uses `java.util.concurrent` package for thread-safe operations

---

**Note**: This implementation demonstrates Exponential Backoff using modern Java concurrency utilities. The algorithm is a fundamental pattern in distributed systems and network programming, helping to build resilient and scalable applications.

