import java.util.List;
import java.util.Map;


public class Category {
    public List<List<String>> queries;
    public Map<String, Category> subcatgories;
    public String catName;

    public Category(String catName) {
        this.catName = catName;
    }
}
