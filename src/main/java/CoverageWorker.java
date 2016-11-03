import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CoverageWorker implements Callable<Integer> {

    private List<List<String>> queries;
    private ExecutorService pool;
    private List<CoveragePerQueryWorker> tasks = new LinkedList<CoveragePerQueryWorker>();

    public CoverageWorker(List<List<String>> queries) {
        this.queries = queries;
        pool = Executors.newFixedThreadPool(queries.size());
        for (List<String> query : queries) {
            tasks.add(new CoveragePerQueryWorker(query));
        }
    }

    @Override
    public Integer call() throws Exception {
        int coverage = 0;
        List<Future<Integer>> futures = pool.invokeAll(tasks);
        pool.shutdownNow();
        Iterator<List<String>> i;
        Iterator<Future<Integer>> j;
        for (i = queries.iterator(), j = futures.iterator(); i.hasNext(); ) {
            assert (j.hasNext());
            List<String> query = i.next();
            Future<Integer> f_count = j.next();
            try {
                Integer count = f_count.get();
                coverage += count;
            } catch (Exception e) {
                System.err.printf("Error %s processing query {", e.toString());
                for (String s : query) {
                    System.err.printf("%s ", s);
                }
                System.err.println("}");
            }
        }
        return coverage;
    }

    static private class CoveragePerQueryWorker implements Callable<Integer> {

        private BingHandler bingHandler = new BingHandler();
        private List<String> query;

        public CoveragePerQueryWorker(List<String> query) {
            this.query = query;
        }

        @Override
        public Integer call() throws Exception {
            return bingHandler.getCount(query);
        }
    }
}
