import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Summary {
    public static void generateSummary(List<Map.Entry<String, Category>> chain) throws Exception {
        Category category;
        String c_name;
        Set<String> pageSet = new TreeSet<String>();
        List<SampleWorker> tasks = new LinkedList<SampleWorker>();
        Iterator<Map.Entry<String, Category>> mapIterator;
        Iterator<Future<Set<String>>> iterator;
        Iterator<Set<String>> pageSetIterator;
        Map.Entry<String, Category> subEntry;

        category = Main.root;
        mapIterator = chain.iterator();
        while (mapIterator.hasNext()) {
            if (category.subcatgories == null) {
                break;
            }
            List<List<String>> queries = new LinkedList<List<String>>();
            for (Map.Entry<String, Category> sub : category.subcatgories.entrySet()) {
                queries.addAll(sub.getValue().queries);
            }
            tasks.add(new SampleWorker(queries));
            subEntry = mapIterator.next();
            category = subEntry.getValue();
        }

        ExecutorService pool = Executors.newFixedThreadPool(tasks.size());
        List<Future<Set<String>>> f_urls = pool.invokeAll(tasks);
        List<Set<String>> samples = new LinkedList<Set<String>>();
        pool.shutdownNow();

        Set<String> prev = null;
        c_name = "Root";
        mapIterator = chain.iterator();
        iterator = f_urls.iterator();

        while (iterator.hasNext()) {
            Set<String> pages = iterator.next().get();

            System.out.printf("Creating Content Summary for %s: Category <%s>\n", pages.size(), c_name);

            if (prev != null) {
                prev.addAll(pages);
            }
            samples.add(pages);
            pageSet.addAll(pages);
            prev = pages;
            subEntry = mapIterator.next();
            c_name = subEntry.getKey();
        }

        Map<String, Set<String>> page_words = new SummaryWorker(pageSet).call();

        for (c_name = "Root", mapIterator = chain.iterator(), pageSetIterator = samples.iterator(); ; subEntry = mapIterator.next(), c_name = subEntry.getKey()) {
            Set<String> pages = pageSetIterator.next();
            Map<String, Integer> freqMap = new TreeMap<String, Integer>();
            System.out.printf("Fetched pages for %s (%d pages)\n", c_name, pages.size());

            for (String str : pages) {
                Set<String> words = page_words.get(str);
                if (words == null) {
                    System.out.printf("\tERROR: %s\n", str);
                } else {
                    System.out.printf("\tGetting page: %s\n", str);
                    for (String word : words) {
                        Integer freq = freqMap.get(word);
                        if (freq != null) {
                            freqMap.put(word, freq + 1);
                        } else {
                            freqMap.put(word, 1);
                        }
                    }
                }
            }

            PrintStream output = new PrintStream(new FileOutputStream(new File(c_name + "-" + Main.host + ".txt")));

            for (Map.Entry<String, Integer> freqEntry : freqMap.entrySet()) {
                output.printf("%s#%d\n", freqEntry.getKey(), freqEntry.getValue());
            }
            if(!pageSetIterator.hasNext()){
                break;
            }
        }
    }
}

