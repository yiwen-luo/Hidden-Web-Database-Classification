import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        if (args.length != 4) {
            throw new IllegalArgumentException("Input format <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>");
        }

        final String bingKey = args[0];
        final double tes = Double.valueOf(args[1]);
        final int tec = Integer.valueOf(args[2]);
        final String host = args[3];

        // Parsing categories
        CategoryHandler categoryHandler = new CategoryHandler("Root", 2);
        Category root = categoryHandler.parseCategories();

        // Classify root
        BingHandler bingHandler = new BingHandler(bingKey, host);
        Classifier classifier = new Classifier(bingHandler, tes, tec);
        List<Map.Entry<String, Category>> resultChain = classifier.classify(root);
        System.out.print("Site classified as Root");
        for(Map.Entry<String, Category> e : resultChain){
            System.out.printf("/%s", e.getKey());
        }
    }


}
