import java.io.PrintStream;
import java.util.List;
import java.util.Map;


public class Category {
    public List<List<String>> queries;
    public Map<String, Category> subcatgories;

    private static void indent(PrintStream out, int level) {
        for (int i = 0; i < level; ++i) {
            out.print("  ");
        }
    }

    private void _print(PrintStream out, Category cate, int level) {
        indent(out, level);
        out.println("Queries:");
        if (cate.queries != null) {
            for (List<String> q : cate.queries) {
                indent(out, level);
                for (String t : q) {
                    out.printf("%s ", t);
                }
                out.println();
            }
        }
        indent(out, level);
        out.println("subcategories");
        if (cate.subcatgories != null) {
            for (Map.Entry<String, Category> e : cate.subcatgories.entrySet()) {
                indent(out, level);
                out.printf("%s: \n", e.getKey());
                _print(out, e.getValue(), level + 1);
            }
        }
    }

    public void print(PrintStream out) {
        _print(out, this, 0);
    }
}
