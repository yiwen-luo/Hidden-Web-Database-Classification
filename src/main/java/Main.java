public class Main {

    public static void main(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Input format <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>");
        }

        final String bingKey = args[0];
        final double tes = Double.valueOf(args[1]);
        final int tec = Integer.valueOf(args[2]);
        final String host = args[3];



    }


}
