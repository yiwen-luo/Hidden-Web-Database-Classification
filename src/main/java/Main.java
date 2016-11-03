import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {
    public static String bingKey, host;
    public static double tes;
    public static int tec;
    public static Category root;

    public Main() {
        this.bingKey = "";
        tes = 0;
        tec = 0;
        host = "";
        root = null;
    }
//IOException, InterruptedException, ExecutionException
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            throw new IllegalArgumentException("Input format <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>");
        }
        String debug_level = System.getenv().get("DEBUG");

        bingKey = args[0];
        tes  = Double.valueOf(args[1]);
        tec = Integer.valueOf(args[2]);
        host = args[3];

        // Parsing categories
        CategoryHandler categoryHandler = new CategoryHandler("Root", 2);
        root = categoryHandler.parseCategories();

        // Classify root
        BingHandler bingHandler = new BingHandler();
        Classifier classifier = new Classifier(bingHandler, tes, tec);
        List<Map.Entry<String, Category>> resultChain = classifier.classify(root);
        System.out.print("Site classified as Root");
        for(Map.Entry<String, Category> e : resultChain){
            System.out.printf("/%s", e.getKey());
        }
        System.out.println();
        System.out.println("\nExtracting topic content summaries...");
        Summary.generateSummary(resultChain);

    }


}
