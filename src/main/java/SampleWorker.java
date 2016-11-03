/**
 * Created by zhangzhiwang on 11/2/16.
 */
import java.util.*;
import java.util.concurrent.*;

public class SampleWorker implements Callable<Set<String>> {

    static final long timeout = 10; //5 sec timeout

    static private class SamplePerQueryWorker implements Callable<String[]>{

        BingHandler bs;
        List<String> query;

        public SamplePerQueryWorker(List<String> query){
            bs = new BingHandler();
            this.query = query;
        }

        @Override
        public String[] call() throws Exception {
            return bs.getTop(query);
        }
    }

    List<List<String>> queries;
    ExecutorService pool;
    List<SamplePerQueryWorker> tasks = new LinkedList<SamplePerQueryWorker>();

    public SampleWorker(List<List<String>> _queries){
        queries = _queries;
        pool = Executors.newFixedThreadPool(queries.size());
        for(List<String> q : queries){
            tasks.add(new SamplePerQueryWorker(q));
        }
    }

    @Override
    public Set<String> call() throws Exception {
        Set<String> all_pages = new TreeSet<String>();
        List<Future<String[]>> ret = pool.invokeAll(tasks);
        pool.shutdownNow();
        Iterator<List<String>> i;
        Iterator<Future<String[]>> j;
        for(i=queries.iterator(), j=ret.iterator();i.hasNext();){
            assert (j.hasNext());
            List<String> query = i.next();
            Future<String[]> f_pages = j.next();
            try{
                String[] pages = f_pages.get();
                for(String p : pages){
                    all_pages.add(p);
                }
            }
            catch(Exception e){
                System.err.printf("Error %s\n", e.toString());
            }
        }
        return all_pages;
    }
}
