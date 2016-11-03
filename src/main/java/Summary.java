import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Summary {
    public static void generateSummary(List<Map.Entry<String, Category>> chain) throws Exception {
        Category category;
        String categoryName;
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
        categoryName = "Root";
        mapIterator = chain.iterator();
        iterator = f_urls.iterator();

        while (iterator.hasNext()) {
            Set<String> pages = iterator.next().get();
            if (prev != null) {
                prev.addAll(pages);
            }
            samples.add(pages);
            pageSet.addAll(pages);
            prev = pages;
            subEntry = mapIterator.next();
            categoryName = subEntry.getKey();
        }

        Map<String, Set<String>> pageWords = new SummaryWorker(pageSet).call();

        categoryName = "Root";
        mapIterator = chain.iterator();
        pageSetIterator = samples.iterator();

        while (pageSetIterator.hasNext()) {
            Set<String> pages = pageSetIterator.next();
            Map<String, Integer> freqMap = new TreeMap<String, Integer>();
            System.out.printf("Fetched pages for %s (%d pages)\n", categoryName, pages.size());

            for (String str : pages) {
                Set<String> words = pageWords.get(str);
                if (words == null) {
                    System.out.printf("\tERROR: %s\n", str);
                } else {
                    System.out.printf("Getting page: %s\n", str);
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

            PrintStream output = new PrintStream(new FileOutputStream(new File(categoryName + "-" + Main.host + ".txt")));

            for (Map.Entry<String, Integer> freqEntry : freqMap.entrySet()) {
                output.printf("%s#%d\n", freqEntry.getKey(), freqEntry.getValue());
            }

            subEntry = mapIterator.next();
            categoryName = subEntry.getKey();
        }
    }
}

