import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;


public class PairProductCalculator implements Callable<Set<Integer>> {
    private final int[] subArray;
    private final String threadName;

    public PairProductCalculator(int[] subArray) {
        this.subArray = subArray;
        this.threadName = Thread.currentThread().getName();
    }

    @Override
    public Set<Integer> call() throws Exception {
        Set<Integer> uniqueProducts = new CopyOnWriteArraySet<>(); 
        
        System.out.println(threadName + 
                           ": Починає обробку " + subArray.length + " елементів.");

        for (int i = 0; i < subArray.length - 1; i += 2) {
            int firstElement = subArray[i];
            int secondElement = subArray[i + 1];
            int product = firstElement * secondElement;
            uniqueProducts.add(product);
        }
        
        System.out.println(threadName + 
                           ": Завершив обробку. Знайдено " + uniqueProducts.size() + " унікальних добутків.");
        
        return uniqueProducts;
    }
}