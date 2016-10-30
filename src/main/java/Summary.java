/**
 * Created by zhangzhiwang on 10/29/16.
 */
import java.io.*;
import java.util.*;
import java.io.OutputStreamWriter;
public class Summary {
    // key -> sample name     value -> pages
    Map<String, Set<String>> map;

    public Summary(Map<String, Set<String>> map) {
        this.map = map;
    }

    public SortedMap<String, Set<String>> retrievePageInfo(Set<String> pages) {
        // key -> word name, value -> the set of document contain that word
        SortedMap<String, Set<String>> map = new TreeMap<>();
        for (String page : pages) {
            Set<String> set = getWordsLynx.runLynx(page);
            for (String word : set) {
                if (map.containsKey(word)) {
                    Set<String> pageList = map.get(word);
                    pageList.add(page);
                } else {
                    Set<String> newSet = new HashSet<>();
                    newSet.add(page);
                }
            }
        }
        return map;
    }

    public SortedMap<String, Integer> computeDocFreq(SortedMap<String, Set<String>> map) {
        SortedMap<String, Integer> docFreq = new TreeMap<>();
        for (String word : map.keySet()) {
            docFreq.put(word, map.get(word).size());
        }
        return docFreq;
    }

    public static void createTextFile (String file, SortedMap<String, Integer> map) throws IOException {
        String fileName = file + ".txt";
        File fout = new File(fileName);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String word : map.keySet()) {
            bw.write(word + " " + String.valueOf(map.get(word)));
            bw.newLine();
        }
        bw.close();
    }

}
