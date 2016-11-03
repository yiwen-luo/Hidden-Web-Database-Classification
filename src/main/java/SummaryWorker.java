/**
 * Created by zhangzhiwang on 11/2/16.
 */
import java.util.*;
import java.util.concurrent.*;

public class SummaryWorker implements Callable<Map<String, Set<String>>>{

    static final long timeout = 10;

    Set<String> pages;
    ExecutorService pool;
    List<SummaryPerPageWorker> tasks = new LinkedList<SummaryPerPageWorker>();

    static private class SummaryPerPageWorker implements Callable<Set<String>>{
        String url;
        public SummaryPerPageWorker(String _url){
            url = _url;
        }

        @Override
        public Set<String> call() throws Exception {
            return getWordsLynx.runLynx(url);
        }
    }

    public SummaryWorker(Set<String> _pages){
        pages = _pages;
        pool = Executors.newFixedThreadPool(pages.size());
        for(String p : pages) {
            tasks.add(new SummaryPerPageWorker(p));
        }
    }

    @Override
    public Map<String, Set<String>> call() throws Exception {
        Map<String, Set<String>> summary = new TreeMap<String, Set<String>>();
        List<Future<Set<String>>> ret = pool.invokeAll(tasks);
        pool.shutdownNow();
        Iterator<String> i;
        Iterator<Future<Set<String>>> j;
        for(i=pages.iterator(), j=ret.iterator();i.hasNext();){
            assert (j.hasNext());
            String page = i.next();
            Future<Set<String>> f_words = j.next();
            try{
                Set<String> words = f_words.get();
                summary.put(page, words);
                /*if(Main.DEBUG >= 2) {
                    System.out.printf("[Summary] Processed %s summary: {", page);
                    for (String w : words) {
                        System.out.printf("%s ");
                    }
                    System.out.println("}");
                }*/
            }
            catch(Exception e){
                System.err.printf("[Summary] Error %s Processing %s\n", e.toString(), page);
            }
        }
        return summary;
    }
}
