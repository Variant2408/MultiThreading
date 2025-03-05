# MultiThreading
**Producer and Consumer Problem**
  The Producer-Consumer problem is a classic example of a multi-threading problem where synchronization between threads is required. It involves two types of threads:).<br/>
  <br/>
    1.**Producer**: This thread generates data (or items) and adds it to a shared resource (like a buffer or queue).<br/>
    2. **Consumer**: This thread consumes the data produced by the producer from the shared resource.).<br/>
    </br>
    **The challenge is to ensure that producers do not produce more data than the consumer can consume (i.e., the buffer should not overflow), and consumers do not consume data that isn't yet produced (i.e., the buffer should not underflow). Proper synchronization is needed to prevent race conditions, where multiple threads access shared resources simultaneously in a way that leads to unexpected results.**<br/>
<br/>
    **Key Concepts**<br/>
  **_Buffer (or Queue)_:** A shared resource where the producer places items, and the consumer takes items from.<br/>
  **_Synchronization_:** Mechanisms like locks, semaphores, or conditions are used to ensure that producers and consumers do not interfere with each other.<br/>
  **_Blocking_:** If the buffer is full, the producer should wait until there is space. If the buffer is empty, the consumer should wait until there are items to consume.<br/>


**Thread safety in java** is the process to make our program safe to use in multithreaded environment, there are different ways through which we can make our program thread safe.

1. *Synchronization is the easiest and most widely used tool for thread safety in java.*<br>
2. *Use of Atomic Wrapper classes from java.util.concurrent.atomic package. For example AtomicInteger*<br>
3. *Use of locks from java.util.concurrent.locks package.*<br>
4. *Using thread safe collection classes, check this post for usage of ConcurrentHashMap for thread safety.*<br>
5. *Using volatile keyword with variables to make every thread read the data from memory, not read from thread cache.*<br>

<ins>*We should not use any object that is maintained in a constant pool, for example String should not be used for synchronization because if any other code is also locking on same String, it will try to acquire lock on the same reference object from String pool and even though both the codes are unrelated, they will lock each other.*</ins>

## Tread Pool
A Thread Pool is a group of pre-instantiated, reusable threads that execute tasks instead of creating new threads every time. This improves performance and resource management, especially in multi-threaded applications.

Java provides ThreadPoolExecutor, which is the primary implementation of a thread pool.

### 1️⃣ What is a Thread Pool?
A Thread Pool maintains multiple worker threads to perform tasks concurrently. Instead of creating a new thread for each task, it reuses existing threads.

##### Why Use a Thread Pool?
*  ✅ Better Performance - Avoids the overhead of creating/destroying threads.
*  ✅ Efficient Resource Usage - Controls the number of active threads.
*  ✅ Prevents System Overload - Avoids excessive thread creation that can slow down the system.

  ### 2️⃣ ThreadPoolExecutor - The Core of Thread Pools
ThreadPoolExecutor is the most flexible way to create and manage thread pools.

###### Creating a ThreadPoolExecutor
```java
import java.util.concurrent.*;

public class ThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(
            2,          // Core pool size (minimum number of threads)
            5,          // Maximum pool size
            60L,        // Keep-alive time for extra threads
            TimeUnit.SECONDS, 
            new LinkedBlockingQueue<>(10)  // Task queue (waiting tasks)
        );

        // Submitting tasks to the thread pool
        for (int i = 1; i <= 10; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " is executing a task");
                try {
                    Thread.sleep(2000); // Simulating work
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown(); // Gracefully shut down the pool after task completion
    }
}

```
#### Output (Example)
```java
pool-1-thread-1 is executing a task
pool-1-thread-2 is executing a task
pool-1-thread-1 is executing a task
pool-1-thread-2 is executing a task
...
```
**✅ Key Points:**

*  **Core Pool Size (2)**: Minimum threads always running.
*  **Max Pool Size (5)**: If more tasks arrive, extra threads are created (up to 5).
*  **Keep-alive Time (60s)**: Extra threads (beyond 2) are destroyed if idle for 60s.
*  **Task Queue (10)**: Holds waiting tasks when all threads are busy.

## 3️⃣ Thread Pool Types in Java (Executors Factory)
Java provides Executors class for easier thread pool creation.

### 1. Fixed Thread Pool
*  🔹 Use when the number of tasks is stable
  ```java
ExecutorService fixedPool = Executors.newFixedThreadPool(3);
 ```
*  Creates a pool with a fixed number of threads.
*  Extra tasks wait in the queue until a thread becomes available.

### 2. Cached Thread Pool
* 🔹 Use when tasks are unpredictable in number
```java
ExecutorService cachedPool = Executors.newCachedThreadPool();

```
*  Creates new threads as needed.
*  Reuses idle threads instead of creating new ones.
*  No task queue, so it scales dynamically.

### 3. Single Thread Executor
* 🔹 Use for sequential task execution
```java
ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

```
*  Only one thread processes tasks one by one.
  
### 4. Scheduled Thread Pool
*  🔹 Use for delayed or periodic tasks.
```java
ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
```
*  Schedules tasks with delays or periodic execution.

#### Example: Running a Task Every 2 Seconds
```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("Executing periodic task...");
}, 0, 2, TimeUnit.SECONDS);
```
##### 4️⃣ Comparing Different Thread Pools
|Pool Type|	Threads|	Use Case|
| --- | --- |--- |
|FixedThreadPool|	Fixed number|	Stable workloads|
|CachedThreadPool|	Dynamic	|Large, unpredictable workloads|
|SingleThreadExecutor|	1|	Sequential execution|
|ScheduledThreadPool|	Fixed number|	Scheduled/Periodic tasks|

### 5️⃣ How Tasks Are Executed in ThreadPoolExecutor
*  A task is submitted to the thread pool.
*  If there are idle threads, the task is assigned immediately.
*  If all threads are busy, the task goes to the queue.
*  If the queue is full, the pool creates new threads (up to the max pool size).
*  If max threads are reached, the task is rejected.

### 6️⃣ Gracefully Shutting Down a Thread Pool
Always shut down the thread pool after use to free resources.
```java
executor.shutdown(); // Initiates an orderly shutdown (waits for tasks to finish)

if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
    executor.shutdownNow(); // Forces shutdown, interrupts running tasks
}

```
### 7️⃣ Custom Rejection Policy in ThreadPoolExecutor
When tasks exceed max threads & queue size, new tasks are rejected. You can handle rejected tasks using RejectedExecutionHandler.

##### Example: Custom Rejection Policy
```java
ExecutorService executor = new ThreadPoolExecutor(
    2, 4, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2),
    new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Task rejected: " + r.toString());
        }
    }
);
```
![image](https://github.com/user-attachments/assets/183ca473-53ab-4e88-b779-f438c3187af6)
![image](https://github.com/user-attachments/assets/53bc47b0-6dea-4192-92e7-533a47fed0d7)
![image](https://github.com/user-attachments/assets/686f9e2c-db28-4405-b3ed-fbc878e69aee)
![image](https://github.com/user-attachments/assets/df0ec0ca-9060-46f8-83b3-f6caa7cf11a2)
![image](https://github.com/user-attachments/assets/819803e5-b6ed-4f4a-8185-3eefb098569a)
![image](https://github.com/user-attachments/assets/159f8d31-3672-4314-bbcd-9bfd0c35f1e2)


###### 8️⃣ Key Takeaways
*  ✅ ThreadPoolExecutor is a customizable thread pool.
*  ✅ Use Executors for easy thread pool creation.
*  ✅ Thread pools reuse threads to improve performance.
*  ✅ Always shut down thread pools to free resources.
*  ✅ Use custom rejection policies for handling overloads.


### Future and Callable in Java
Future and Callable are used in Java's concurrent programming to handle tasks that run asynchronously and return results.

### 1️⃣ Callable Interface
```Callable<T>``` is similar to Runnable, but:

*  It **returns a result**.
*  It can throw **checked exceptions**.
*  It is executed by an ExecutorService.

```java
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Creating a Callable task
        Callable<Integer> task = () -> {
            System.out.println("Task is running...");
            Thread.sleep(2000);
            return 42; // Returning result
        };

        // Submitting task to ExecutorService
        Future<Integer> future = executor.submit(task);

        System.out.println("Task submitted, waiting for result...");

        // Getting result from Future (blocks until result is available)
        int result = future.get();
        System.out.println("Task completed! Result: " + result);

        executor.shutdown();
    }
}
```
* • Runnable do not have any Return type.
* • Callable has the capability to return the value.

  ### 2️⃣ Future Interface

  ```Future<T>``` represents the result of an asynchronous computation.

*  It provides methods to check if the computation is complete.
*  It allows retrieving the result **once it's available**.
*  It can be **cancelled**.

#### Future Methods
| Method	|Description |
| --- | --- |
|get()|	Blocks and waits for the result.|
|get(long timeout, TimeUnit unit)|	Waits for the result with a timeout.|
|isDone()|	Returns true if the task is completed.|
|isCancelled()|	Returns true if the task was cancelled.|
|cancel(boolean mayInterrupt)|	Cancels the task if possible.|

```java
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        Callable<String> task = () -> {
            Thread.sleep(3000);
            return "Task Completed!";
        };

        Future<String> future = executor.submit(task);

        System.out.println("Task submitted...");

        while (!future.isDone()) {
            System.out.println("Waiting for task to complete...");
            Thread.sleep(1000);
        }

        System.out.println("Result: " + future.get());  // Blocking call

        executor.shutdown();
    }
}
 ```
#### Key Points About Future
*  ✅ It stores the result of Callable.
*  ✅ It can be used to check task completion (isDone()).
*  ✅ It can be used to cancel the task (cancel()).

#### 3️⃣ Limitation of Future (No Callbacks)
*  Blocking Nature: future.get() blocks until the result is available.
*  No Automatic Callbacks: There is no way to execute an action once the result is ready without blocking.

## CompletableFuture in Java (Java 8+)
CompletableFuture is an advanced implementation of Future that allows:

*  **Non-blocking asynchronous programming.**
*  **Chaining multiple computations.**
*  **Automatic callbacks when the result is ready (unlike Future.get() which blocks).**
*  **Combining multiple tasks.**

#### 1️⃣ Creating a Simple CompletableFuture
A CompletableFuture runs a task asynchronously in the background.
##### Example: Basic CompletableFuture
```java 
import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executing task...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello from CompletableFuture!";
        });

        // Non-blocking - Main thread is free
        System.out.println("Main thread is not blocked!");

        // Getting result (blocks if not ready)
        System.out.println("Result: " + future.get());
    }
}

```
#### output
```ruby
Main thread is not blocked!
Executing task...
Result: Hello from CompletableFuture!
```

*  supplyAsync method initiates an Async operation.
*  'supplier' is executed asynchronously in a separate thread.
*  If we want more control on Threads, we can pass Executor in the method.
*  By default its uses, shared Fork-Join Pool executor. It dynamically adjust its pool size based on processors.
* **✅ Key Point: supplyAsync() runs asynchronously, get() waits for the result.**

#### 2️⃣ Chaining Multiple Computations
You can process the result once it's available using .thenApply(), without blocking.
#### Example: Transforming the Result.
```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureChaining {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Hello";
        }).thenApply(result -> result + " World"); // Chaining another computation

        System.out.println(future.get()); // Output: Hello World
    }
}
```
*  **✅ Key Point: .thenApply() modifies the result of the previous step.**

#### 3️⃣ Running a Task Without Returning a Value
If you don’t need to return a result, use .thenRun() or .thenAccept().

##### Example: Running a Task After Completion
```java 
import java.util.concurrent.CompletableFuture;

public class CompletableFutureThenRun {
    public static void main(String[] args) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> "Task Done!")
                .thenRun(() -> System.out.println("Another task executed!")); // Runs after first task

        future.join(); // Ensures the program waits for completion
    }
}

```
*  **✅ Key Point: .thenRun() runs a task after the previous one completes.**

#### 4️⃣ Combining Multiple CompletableFutures
You can combine multiple async tasks using **thenCombine()**.

#### Example: Combining Two Async Tasks
```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureCombine {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

        CompletableFuture<String> combined = future1.thenCombine(future2, (res1, res2) -> res1 + " " + res2);

        System.out.println(combined.get()); // Output: Hello World
    }
}
```

*  **✅ Key Point: .thenCombine() merges two futures.**

#### 5️⃣ Handling Exceptions in CompletableFuture
CompletableFuture provides **.exceptionally()** and **.handle()** to recover from errors.

#### Example: Handling an Exception
```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureExceptionHandling {
    public static void main(String[] args) throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Oops!");
            return 42;
        }).exceptionally(ex -> {
            System.out.println("Exception: " + ex.getMessage());
            return 0; // Default value
        });

        System.out.println("Result: " + future.get()); // Output: Exception: Oops! Result: 0
    }
}

```

*  **✅ Key Point: .exceptionally() catches errors and provides a fallback value.**

#### 6️⃣ Running Multiple Tasks in Parallel
Use **.allOf()** to run multiple tasks in parallel and wait for all to finish.

####  Example: Running Tasks in Parallel

```java
import java.util.concurrent.CompletableFuture;

public class CompletableFutureAllOf {
    public static void main(String[] args) {
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> System.out.println("Task 1")),
            CompletableFuture.runAsync(() -> System.out.println("Task 2")),
            CompletableFuture.runAsync(() -> System.out.println("Task 3"))
        );

        allTasks.join(); // Waits for all tasks to complete
    }
}

```
*  **✅ Key Point: .allOf() ensures all tasks complete before proceeding.**

  #### Summary
|Feature	|Future|	CompletableFuture|
| --- | --- |--- |
|Non-blocking	|❌ No|	✅ Yes|
|Callback support|	❌ No|	✅ Yes (thenApply, thenAccept)|
|Combining tasks|	❌ No|	✅ Yes (thenCombine, allOf)|
|Error handling|	❌ No|	✅ Yes (exceptionally, handle)|
|Parallel execution|	❌ No|	✅ Yes (allOf, anyOf)|

####  When to Use What?
*  Use Future if you only need a basic async computation.
*  Use CompletableFuture if you need chaining, non-blocking execution, and better error handling.

  ## ForkJoinPool / Work Stealing Pool Executor

![image](https://github.com/user-attachments/assets/5cc5f842-729f-402d-9856-8c9693b8f3ef)
![image](https://github.com/user-attachments/assets/35ed4bc9-636e-4c11-a8e5-26d573a2d52b)
![image](https://github.com/user-attachments/assets/41ed3194-884f-4567-8367-52430653cb3c)

```java
class ComputeSumTask extends RecursiveTask<Integer> {
  int start;
  int end;
  ComputeSumTask(int start, int end) (
    this.start start;
    this.end end;
)
  @Override
  protected Integer compute(){
    if (end-start <= 4){
      int totalsum=0;
      for (int i= start; i < end; i++) {
        totalSum+= 1;
      return totalsum;
  }
  else {
//split the task
    int mid (start end)/2;
  ComputeSumTask LeftTask = new ComputeSumTask(start, mid);
  ComputeSumTask rightTask = new ComputeSumTask(mid + 1, end);

    // Fork the subtasks for parallel execution:
  LeftTask.fork();
  rightTask.fork();

    // Combine the results of subtasks
  int LeftResult leftTask.join();
  int rightResult rightTask.join();

  // Continue the results
  return LeftResult rightResult;
}
}
}

```
### ForkJoinPool in Java
ForkJoinPool is a specialized thread pool designed for parallel execution of tasks using the divide-and-conquer approach. It is part of Java's Fork/Join Framework, introduced in Java 7.

##### 📌 When to Use ForkJoinPool?
*  Best for recursive and parallelizable tasks.
*  Ideal for CPU-intensive operations like sorting, matrix multiplication, or searching.
*  Efficient when tasks can be split into smaller independent subtasks.

#### 1️⃣ Fork/Join Framework Overview
*   🔹 Uses a **work-stealing algorithm**, where idle threads "steal" work from busy threads.
*   🔹 Breaks a large task into smaller sub-tasks, processes them in parallel, and then combines results.

#### 2️⃣ Creating a ForkJoinPool
  We can create a ForkJoinPool using:
```java
ForkJoinPool pool = new ForkJoinPool(); // Uses available processor cores
```
or specify the number of worker threads:
```java
ForkJoinPool pool = new ForkJoinPool(4); // Uses available processor cores
```
#### 3️⃣ ForkJoinTask (RecursiveTask vs RecursiveAction)
  Java provides two abstract classes for ForkJoinTask:

1. **RecursiveTask<V> → Returns a result (for computations).**
2. **RecursiveAction → No result (for tasks like modifying an array).**

#### 🔹 Example 1: RecursiveTask (Summing an Array)
```java
import java.util.concurrent.*;

class SumTask extends RecursiveTask<Integer> {
    private int[] arr;
    private int start, end;
    private static final int THRESHOLD = 2; // Smallest chunk size

    public SumTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if (end - start <= THRESHOLD) {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        } else {
            int mid = (start + end) / 2;
            SumTask leftTask = new SumTask(arr, start, mid);
            SumTask rightTask = new SumTask(arr, mid, end);

            leftTask.fork(); // Asynchronously execute left subtask
            int rightResult = rightTask.compute(); // Compute right subtask synchronously
            int leftResult = leftTask.join(); // Wait for left subtask to complete

            return leftResult + rightResult;
        }
    }
}

public class ForkJoinExample {
    public static void main(String[] args) {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};
        ForkJoinPool pool = new ForkJoinPool();

        SumTask task = new SumTask(numbers, 0, numbers.length);
        int result = pool.invoke(task); // Start computation

        System.out.println("Sum: " + result);
    }
}
```
###### 🔹 Explanation
*  ✔ The array is recursively divided until it reaches the THRESHOLD.
*  ✔ Uses fork() to run tasks asynchronously.
*  ✔ Uses join() to combine results.
*  ✔ invoke(task) submits the task to ForkJoinPool.

##### 🔹 Example 2: RecursiveAction (Parallel Sorting)
```java
import java.util.concurrent.*;

class SortTask extends RecursiveAction {
    private int[] arr;
    private int start, end;
    private static final int THRESHOLD = 3;

    public SortTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start <= THRESHOLD) {
            Arrays.sort(arr, start, end); // Directly sort small portions
        } else {
            int mid = (start + end) / 2;
            SortTask leftTask = new SortTask(arr, start, mid);
            SortTask rightTask = new SortTask(arr, mid, end);

            invokeAll(leftTask, rightTask); // Execute in parallel
        }
    }
}

public class ParallelSortExample {
    public static void main(String[] args) {
        int[] numbers = {8, 3, 5, 2, 7, 6, 4, 1};
        ForkJoinPool pool = new ForkJoinPool();

        SortTask task = new SortTask(numbers, 0, numbers.length);
        pool.invoke(task);

        System.out.println("Sorted Array: " + Arrays.toString(numbers));
    }
}
```

##### 4️⃣ Key Methods in ForkJoinPool
|Method|	Description|
| ---| ---|
|fork()|	Asynchronously starts a new subtask.|
|join()|	Waits for a subtask to complete and returns its result.|
|invoke(task)|	Submits and executes a task.|
|invokeAll(t1,t2)|	Runs multiple tasks in parallel.|

##### 5️⃣ ForkJoinPool vs. ThreadPoolExecutor
|Feature|	ForkJoinPool|	ThreadPoolExecutor|
| ---| ---| ---|
|Best For|	Recursive, parallel tasks|	Independent tasks|
|Work Stealing|	✅ Yes (idle threads steal work)|	❌ No (static allocation)|
|Task Type|	Dependent (splittable) tasks|	Independent tasks|
|Performance|	Faster for CPU-bound tasks|	Suitable for I/O-bound tasks|

#### 6️⃣ When to Use ForkJoinPool?
*  ✅ Best for Recursive Computations (e.g., parallel sorting, matrix multiplication).
*  ✅ Heavy CPU-intensive tasks that can be split into independent subtasks.
*  ✅ Work-stealing mechanism prevents idle threads.
*  🚫 Avoid for simple thread management, use ThreadPoolExecutor instead.

#### 7️⃣ Summary
*  ✔ ForkJoinPool is a parallel execution framework in Java.
*  ✔ RecursiveTask returns results, RecursiveAction does not.
*  ✔ Uses fork/join for parallel processing.
*  ✔ Work-stealing allows efficient CPU utilization.
*  ✔ Best suited for divide-and-conquer problems.

### virtual threads vs Normal threads




