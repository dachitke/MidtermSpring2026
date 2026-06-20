import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import persistence.GameHistoryRepository;
import persistence.GameResult;
import persistence.PersistenceManager;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();
        int target = 500;
        boolean statsOnly = false;
        boolean persist = true;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--target") && i + 1 < args.length) {
                target = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--stats")) {
                statsOnly = true;
            } else if (args[i].equals("--no-db")) {
                persist = false;
            }
        }

        // Report mode: print persisted history and exit without playing.
        if (statsOnly) {
            log.info("Stats mode: printing persisted game history");
            try {
                GameHistoryRepository repo = new GameHistoryRepository(PersistenceManager.getEntityManagerFactory());
                HistoryReport.printAll(repo, 10);
            } finally {
                PersistenceManager.close();
            }
            return;
        }

        log.info("Application start: bots={}, games={}, human={}, seed={}, target={}, persist={}",
                bots, games, human, seed, target, persist);

        GameHistoryRepository repo = persist
                ? new GameHistoryRepository(PersistenceManager.getEntityManagerFactory())
                : null;

        try {
            for (int g = 1; g <= games; g++) {
                System.out.println("\n=== Game " + g + " (to " + target + " points) ===");

                log.info("Starting game {} of {}", g, games);

                Game game = new Game(bots, human, seed + g, target);
                GameResult result = game.playGame();

                if (repo != null && result != null) {
                    persistResult(repo, result);
                }
            }
        } finally {
            if (persist) {
                PersistenceManager.close();
            }
        }

        log.info("Application end: completed {} game(s)", games);
    }

    /** Persistence failures must never break gameplay. */
    private static void persistResult(GameHistoryRepository repo, GameResult result) {
        try {
            Long id = repo.save(result);
            log.info("Persisted game id={}, winner={}", id, result.getWinnerName());
        } catch (RuntimeException e) {
            log.warn("Failed to persist game result: {}", e.toString());
        }
    }
}