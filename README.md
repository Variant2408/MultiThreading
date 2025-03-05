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







