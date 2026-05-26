public class Main {

    public static void main(String[] args) {
        int bots = 1;
        int games = 1;
        boolean human = true;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            }
        }

        for (int g = 1; g <= games; g++) {
            System.out.println("\n=== Game " + g + " ===");

            Game game = new Game(bots, human, seed);
            game.playGame();
        }
    }
}