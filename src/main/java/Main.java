import java.util.List;
import java.util.Map;

public class Main {
    public static String bingKey, host;
    public static Category root;

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException("Input format <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>");
        }

        bingKey = args[0];
        host = args[3];

        double tes = Double.valueOf(args[1]);
        int tec = Integer.valueOf(args[2]);

        // Parsing categories
        CategoryHandler categoryHandler = new CategoryHandler("Root", 2);
        root = categoryHandler.parseCategories();

        // Classify root
        Classifier classifier = new Classifier(tes, tec);
        List<Map.Entry<String, Category>> resultChain = classifier.classify(root);
        System.out.println();
        System.out.print("Site classified as Root");
        for (Map.Entry<String, Category> e : resultChain) {
            System.out.printf("/%s", e.getKey());
        }
        System.out.println();
        System.out.println("\nExtracting topic content summaries...");
        Summary.generateSummary(resultChain);

    }
}
