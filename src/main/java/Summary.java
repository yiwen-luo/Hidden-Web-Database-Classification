import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Summary {
    public static void generateSummary(List<Map.Entry<String, Category>> chain) throws Exception {
        Set<String> all_pages = new TreeSet<String>();

        List<SampleWorker> tasks = new LinkedList<SampleWorker>();

        Category c;
        String categoryName;
        Iterator<Map.Entry<String, Category>> i;
        Iterator<Future<Set<String>>> iterator;
        Iterator<Set<String>> setIterator;
        Map.Entry<String, Category> next_sub;

        for (c = Main.root, categoryName = "Root", i = chain.iterator(); ; next_sub = i.next(), c = next_sub.getValue(), categoryName = next_sub.getKey()) {
            if (c.subcatgories == null) {
                break;
            }
            List<List<String>> queries = new LinkedList<List<String>>();
            for (Map.Entry<String, Category> sub : c.subcatgories.entrySet()) {
                queries.addAll(sub.getValue().queries);
            }
            tasks.add(new SampleWorker(queries));

            if (!i.hasNext()) {
                break;
            }
        }
        ExecutorService pool = Executors.newFixedThreadPool(tasks.size());

        List<Future<Set<String>>> f_urls = pool.invokeAll(tasks);
        List<Set<String>> samples = new LinkedList<Set<String>>();
        pool.shutdownNow();

        Set<String> prev = null;
        for (c = Main.root, categoryName = "Root", i = chain.iterator(), iterator = f_urls.iterator(); ; next_sub = i.next(), categoryName = next_sub.getKey(), c = next_sub.getValue()) {
            Set<String> pages = iterator.next().get();

            System.out.printf("[Sample]: %d pages for Category <%s>\n", pages.size(), categoryName);

            if (prev != null) {
                prev.addAll(pages);
            }
            samples.add(pages);
            all_pages.addAll(pages);
            prev = pages;

            if (!iterator.hasNext()) {
                break;
            }
        }
        Map<String, Set<String>> page_words = new SummaryWorker(all_pages).call();

        for (c = Main.root, categoryName = "Root", i = chain.iterator(), setIterator = samples.iterator(); ; next_sub = i.next(), categoryName = next_sub.getKey(), c = next_sub.getValue()) {
            Set<String> pages = setIterator.next();
            Map<String, Integer> freq_map = new TreeMap<String, Integer>();
            System.out.printf("[Summary]: Fetched pages for <%s> (%d pages)\n", categoryName, pages.size());
            for (String s : pages) {
                Set<String> words = page_words.get(s);
                if (words == null) {
                    System.out.printf("\tERROR: %s\n", s);
                } else {
                    System.out.printf("\tSUCCESS: %s\n", s);
                    for (String w : words) {
                        Integer freq = freq_map.get(w);
                        if (freq != null) {
                            freq_map.put(w, freq + 1);
                        } else {
                            freq_map.put(w, 1);
                        }
                    }
                }
            }
            PrintStream ps = new PrintStream(new FileOutputStream(new File(categoryName + "-" + Main.host + ".txt")));
            for (Map.Entry<String, Integer> e : freq_map.entrySet()) {
                ps.printf("%s#%d\n", e.getKey(), e.getValue());
            }
            if(!setIterator.hasNext()){
                break;
            }
        }

    }
}