import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CategoryHandler {
    private int MAX_SUB_LEVEL;
    private String rootName;

    public CategoryHandler(String rootFileName, int MAX_SUB_LEVEL) throws IOException {
        this.rootName = rootFileName;
        this.MAX_SUB_LEVEL = MAX_SUB_LEVEL;
    }

    public Category parseCategories() throws IOException {
        Category root = new Category(rootName);
        createCategory(rootName, root, 0);
        return root;
    }

    private void createCategory(String catName, Category root, int subLevel) throws IOException {
        root.subcatgories = new TreeMap<>();
        BufferedReader catReader = new BufferedReader(new FileReader("./data/"+catName.toLowerCase() + ".txt"));
        String currentLine;
        String lastChildName = null;
        Category lastChild = null;
        while ((currentLine = catReader.readLine()) != null) {
            String[] currentLineList = currentLine.split(" ");
            if (!currentLineList[0].equals(lastChildName)) {
                lastChildName = currentLineList[0];
                lastChild = new Category(lastChildName);
                lastChild.queries = new ArrayList<List<String>>();
                root.subcatgories.put(lastChildName, lastChild);
                if (subLevel < MAX_SUB_LEVEL - 1) {
                    createCategory(lastChildName, lastChild, subLevel + 1);
                }
            }
            List<String> queryEntry = new ArrayList<>();
            for (int i = 1; i < currentLineList.length; i++) {
                queryEntry.add(currentLineList[i]);
            }
            lastChild.queries.add(queryEntry);
        }
    }
}
