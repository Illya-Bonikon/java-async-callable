# java-async-array-processor

## Project Goal

[cite\_start]This project was developed to study and demonstrate the principles of **asynchronous programming** and **thread-safe collection management** in Java[cite: 268, 274, 92]. The primary goal is to implement asynchronous processing of a large array of numbers by utilizing modern concurrency mechanisms.

-----

## Theoretical Foundations

### Asynchronicity vs. Multithreading

  * [cite\_start]**Asynchronous Programming** is an approach where a task's execution does not block the program's main thread, allowing the program to continue performing other operations[cite: 283, 406]. [cite\_start]The process of execution does not block the program's work, unlike in synchronous programming[cite: 284].
  * [cite\_start]**Multithreading (Concurrency)** is when multiple threads work on tasks, potentially in parallel[cite: 307]. [cite\_start]Asynchronicity and multithreading can work independently but are often combined effectively[cite: 407].
  * [cite\_start]The difference is that **asynchronicity** is about having more than one task in progress simultaneously, while **concurrency/multithreading** is about having more than one thread working on the tasks in progress[cite: 306, 307].

### Interfaces for Asynchronous Tasks

| Interface | Purpose | Principle of Operation |
| :--- | :--- | :--- |
| **`Callable<V>`** | [cite\_start]Represents a task that **returns a result** of type `V` and can throw an exception[cite: 362, 408, 413]. | [cite\_start]Better than `Runnable` when a result or exception handling is needed[cite: 413]. |
| **`Future<V>`** | [cite\_start]An object representing the **result of an asynchronous computation** that will be available in the future[cite: 377, 408]. | [cite\_start]Allows non-blocking checks (`isDone()`, `isCancelled()`) and a blocking retrieval of the result (`get()`)[cite: 381, 380, 382]. |

### Thread-Safe Collections: Copy-On-Write

  * [cite\_start]The collection **`CopyOnWriteArraySet`** is used for result aggregation[cite: 96, 123].
  * [cite\_start]It is a **thread-safe** implementation optimized for scenarios with **many reads and few writes (modifications)**[cite: 244, 251].
  * [cite\_start]**Copy-On-Write Principle**: All modification operations (add, remove) are implemented by **creating a new copy** of the internal array[cite: 102, 103, 115].
  * [cite\_start]**Iterators**: It uses **fail-safe iterators**[cite: 116, 135, 254]. [cite\_start]Fail-safe iterators do not throw `ConcurrentModificationException` but might not reflect the most recent state of the collection[cite: 254].

-----

## Implementation Details

The project simulates the processing of a large array by splitting it into sub-arrays and asynchronously calculating the **unique pair products** within each part.

### Key Components

| Component | Purpose |
| :--- | :--- |
| **`AsyncArrayProcessor.java`** | The main class. Responsible for initializing the array (size 40-60 elements), configuring the `ExecutorService` (based on available processor cores), submitting tasks, and collecting the final results via `Future` objects. |
| **`PairProductCalculator.java`** | Implements the **`Callable<Set<Integer>>`** interface. It receives a sub-array, calculates pair products (first $\times$ second, third $\times$ fourth, etc.), and returns them as a **`CopyOnWriteArraySet`** (unique results). |
| **`Future` API** | Used to manage the asynchronous results. The program includes explicit checks for the task status using **`isDone()`** and demonstrates the logic for task cancellation using **`cancel()`/`isCancelled()`**. |

-----

## Project Structure

```
java-async-array-processor/
├── src/
│   ├── AsyncArrayProcessor.java    // Main class, Executor and Future logic.
│   └── PairProductCalculator.java  // Callable, pair multiplication logic.
└── README.md
```

-----

## How to Run

1.  Clone the repository:
    ```bash
    git clone https://github.com/YourUsername/java-async-array-processor.git
    cd java-async-array-processor
    ```
2.  Compile the Java files:
    ```bash
    javac src/*.java
    ```
3.  Execute the main simulation class:
    ```bash
    java src/AsyncArrayProcessor
    ```

*The console output demonstrates the parallel processing of array segments by different threads, the task status check via `isDone()`/`isCancelled()`, and the final program execution time.*