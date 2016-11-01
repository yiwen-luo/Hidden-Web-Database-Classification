import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class CategoryHandler {
    BufferedReader categoryReader;

    public CategoryHandler(String categoriesFile) throws IOException {
        this.categoryReader = new BufferedReader(new FileReader(categoriesFile));
    }

    public Category parseCategories() {
        Category root = new Category();

    }

    private void createCategory(String name, Category parent) throws Exception {
        String query_file = getSubCategory(name);
        if (query_file == null) {
            return;
        }
        parent.subcatgories = new TreeMap<String, Category>();
        BufferedReader query_reader = new BufferedReader(new FileReader(query_file));
        String line;
        String last_child_name = null;
        Category last_child = null;
        while ((line = query_reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (!parts[0].equals(last_child_name)) {
                last_child_name = parts[0];
                last_child = new Category();
                last_child.queries = new LinkedList<List<String>>();
                parent.subcatgories.put(last_child_name, last_child);
                createCategory(last_child_name, last_child);
            }
            List<String> query_entry = new LinkedList<String>();
            last_child.queries.add(query_entry);
            for (int i = 1; i < parts.length; ++i) {
                query_entry.add(parts[i]);
            }
        }
    }

    private String getSubCategory(String category) throws IOException {
        String line = categoryReader.readLine();
        assert (line != null);
        String[] parts = line.split("#");
        assert (parts[0].equals(category));
        if (parts[1].equals("NULL")) {
            return null;
        } else {
            return parts[1];
        }
    }
}
