import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SummaryWorker implements Callable<Map<String, Set<String>>> {
    ExecutorService pool;
    List<SummaryPerPage> tasks = new LinkedList<SummaryPerPage>();
    Set<String> pages;

    private static class SummaryPerPage implements Callable<Set<String>> {
        String url;

        public SummaryPerPage(String _url) {
            url = _url;
        }

        @Override
        public Set<String> call() throws Exception {
            return getWordsLynx.runLynx(url);
        }
    }

    public SummaryWorker(Set<String> _pages) {
        pages = _pages;
        pool = Executors.newFixedThreadPool(pages.size());
        for (String page : pages) {
            tasks.add(new SummaryPerPage(page));
        }
    }

    @Override
    public Map<String, Set<String>> call() throws Exception {
        List<Future<Set<String>>> ret = pool.invokeAll(tasks);
        pool.shutdownNow();

        Iterator<String> i;
        Iterator<Future<Set<String>>> iterator;
        Map<String, Set<String>> summary = new TreeMap<String, Set<String>>();

        for (i = pages.iterator(), iterator = ret.iterator(); i.hasNext(); ) {
            assert (iterator.hasNext());
            String page = i.next();
            Future<Set<String>> wordSetIterator = iterator.next();
            try {
                summary.put(page, wordSetIterator.get());
            } catch (Exception e) {
                System.err.printf("Error %s Processing %s\n", e.toString(), page);
            }
        }
        return summary;
    }
}
