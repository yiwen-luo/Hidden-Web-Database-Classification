import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Classifier {

    private double tes;
    private int tec;

    public Classifier(double tes, int tec) {
        this.tes = tes;
        this.tec = tec;
    }

    public List<Map.Entry<String, Category>> classify(Category root) throws InterruptedException, ExecutionException {
        List<Map.Entry<String, Category>> resultChain = new ArrayList<>();
        classifyCategory(root, resultChain, 1.0);
        return resultChain;
    }

    private void classifyCategory(Category category, List<Map.Entry<String, Category>> resultChain, double totalSpec) throws InterruptedException, ExecutionException {
        if (category.subcatgories == null) {
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(category.subcatgories.size());
        Set<Map.Entry<String, Category>> categories = category.subcatgories.entrySet();

        List<CoverageWorker> tasks = new LinkedList<CoverageWorker>();
        Iterator<Map.Entry<String, Category>> i;
        for (i = categories.iterator(); i.hasNext(); ) {
            tasks.add(new CoverageWorker(i.next().getValue().queries));
        }
        List<Future<Integer>> futures = pool.invokeAll(tasks);
        pool.shutdownNow();

        int total_coverage = 0;
        int[] coverages = new int[categories.size()];
        Map.Entry<String, Category> target_category = null;
        int max_coverage = Integer.MIN_VALUE;
        int k;

        Iterator<Future<Integer>> j;
        for (i = categories.iterator(), j = futures.iterator(), k = 0; i.hasNext(); ++k) {
            Map.Entry<String, Category> sub_category = i.next();
            Future<Integer> future = j.next();
            coverages[k] = future.get();
            total_coverage += coverages[k];
            if (coverages[k] > max_coverage) {
                target_category = sub_category;
                max_coverage = coverages[k];
            }
        }
        for (i = categories.iterator(), k = 0; i.hasNext(); ++k) {
            Map.Entry<String, Category> sub_category = i.next();
            System.out.printf("[Classify]: Category <%s> Coverage %d Specificity %f\n", sub_category.getKey(),
                    coverages[k], (double) coverages[k] / (double) total_coverage * totalSpec);
        }

        double specificity = (double) max_coverage / (double) total_coverage * totalSpec;
        if (specificity >= tes && max_coverage >= tec) {
            resultChain.add(target_category);
            classifyCategory(target_category.getValue(), resultChain, specificity);
        }
    }

}
