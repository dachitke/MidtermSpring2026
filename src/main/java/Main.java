import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
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

        log.info("Application start: bots={}, games={}, human={}, seed={}", bots, games, human, seed);

        for (int g = 1; g <= games; g++) {
            System.out.println("\n=== Game " + g + " ===");

            log.info("Starting game {} of {}", g, games);

            Game game = new Game(bots, human, seed);
            game.playGame();
        }

        log.info("Application end: completed {} game(s)", games);
    }
}