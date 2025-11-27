import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class AsyncArrayProcessor {

    private static final int MIN_ARRAY_SIZE = 40;
    private static final int MAX_ARRAY_SIZE = 60;
    private static final int MIN_RANGE = 0;
    private static final int MAX_RANGE = 100;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        
        System.out.println("--- Асинхронна обробка масиву за допомогою Callable та Future ---");

        int arraySize = initializeArraySize();
        int[] mainArray = createAndPopulateArray(arraySize);
        System.out.println("Створено масив з " + arraySize + " елементів. Діапазон значень: [" + MIN_RANGE + "; " + MAX_RANGE + "]");
        System.out.println("Перші 10 елементів масиву для прикладу: " + getArrayPreview(mainArray));

        ExecutorService executor = createExecutorService();
        List<Future<Set<Integer>>> futures = submitTasks(executor, mainArray);

        Set<Integer> finalResults = collectResults(executor, futures);

        terminateExecutor(executor);
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("\n--- Результати ---");
        System.out.println("Загальна кількість унікальних попарних добутків: " + finalResults.size());
        System.out.println("Перші 10 унікальних добутків: " + getCollectionPreview(finalResults));
        System.out.println("Час роботи програми: " + (endTime - startTime) + " мс.");
    }

    private static int initializeArraySize() {
        return new Random().nextInt(MAX_ARRAY_SIZE - MIN_ARRAY_SIZE + 1) + MIN_ARRAY_SIZE;
    }

    private static int[] createAndPopulateArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(MAX_RANGE - MIN_RANGE + 1) + MIN_RANGE;
        }
        return array;
    }

    private static ExecutorService createExecutorService() {
        int cores = Runtime.getRuntime().availableProcessors();
        int poolSize = Math.max(cores, 2); // Мінімум 2 потоки
        System.out.println("Пул потоків створено з " + poolSize + " потоків (завдань).");
        return Executors.newFixedThreadPool(poolSize);
    }
    
    private static List<Future<Set<Integer>>> submitTasks(ExecutorService executor, int[] mainArray) {
        int poolSize = ((ThreadPoolExecutor) executor).getCorePoolSize();
        int arraySize = mainArray.length;
        int partSize = (int) Math.ceil((double) arraySize / poolSize);
        List<Future<Set<Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < arraySize; i += partSize) {
            int end = Math.min(i + partSize, arraySize);
            int[] subArray = Arrays.copyOfRange(mainArray, i, end); 
            
            PairProductCalculator task = new PairProductCalculator(subArray);
            Future<Set<Integer>> future = executor.submit(task);
            futures.add(future);
        }
        return futures;
    }

    private static Set<Integer> collectResults(ExecutorService executor, List<Future<Set<Integer>>> futures) {
        Set<Integer> finalResults = new CopyOnWriteArraySet<>(); 

        for (Future<Set<Integer>> future : futures) {
            
            System.out.println("Статус завдання: isDone=" + future.isDone() + ", isCancelled=" + future.isCancelled());

            try {
                Set<Integer> partResult = future.get(); 
                finalResults.addAll(partResult);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                System.err.println("Потік був перерваний: " + e.getMessage());
            } catch (ExecutionException e) {
                System.err.println("Виняток під час виконання задачі: " + e.getCause().getMessage());
                if (!future.isCancelled()) {
                    future.cancel(true); // Спроба перервати виконання
                    System.out.println("Завдання скасовано? " + future.isCancelled());
                }
            }
        }
        return finalResults;
    }

    private static void terminateExecutor(ExecutorService executor) {
        executor.shutdown(); // Ініціюємо коректне завершення
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Примусове завершення
                System.err.println("Executor не завершив роботу вчасно. Виконано примусове завершення.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private static String getArrayPreview(int[] array) {
        return Arrays.stream(array)
                     .limit(10)
                     .mapToObj(String::valueOf)
                     .collect(Collectors.joining(", ", "[", "...]"));
    }

    private static <T> String getCollectionPreview(Collection<T> collection) {
        return collection.stream()
                         .map(Object::toString)
                         .limit(10)
                         .collect(Collectors.joining(", ", "[", "...]"));
    }
}