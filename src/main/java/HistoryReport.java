import java.time.format.DateTimeFormatter;
import java.util.List;

import persistence.GameHistoryRepository;
import persistence.reports.HighScore;
import persistence.reports.RecentGame;
import persistence.reports.WinCount;

/** Renders the persisted game-history reports to the console. */
final class HistoryReport {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private HistoryReport() {
    }

    static void printAll(GameHistoryRepository repo, int limit) {
        printRecentGames(repo, limit);
        printWinCounts(repo);
        printHighestScores(repo, limit);
    }

    static void printRecentGames(GameHistoryRepository repo, int limit) {
        System.out.println("\n=== Recent Games (last " + limit + ") ===");
        List<RecentGame> games = repo.recentGames(limit);
        if (games.isEmpty()) {
            System.out.println("(no games recorded yet)");
            return;
        }
        for (RecentGame g : games) {
            System.out.printf("#%d  %s  winner=%s  rounds=%d%n",
                    g.gameId(),
                    g.endedAt() != null ? TS.format(g.endedAt()) : TS.format(g.startedAt()),
                    g.winner(),
                    g.roundsPlayed());
        }
    }

    static void printWinCounts(GameHistoryRepository repo) {
        System.out.println("\n=== Player Win Counts ===");
        List<WinCount> wins = repo.winCounts();
        if (wins.isEmpty()) {
            System.out.println("(no winners recorded yet)");
            return;
        }
        for (WinCount w : wins) {
            System.out.printf("%-12s %d%n", w.player(), w.wins());
        }
    }

    static void printHighestScores(GameHistoryRepository repo, int limit) {
        System.out.println("\n=== Highest Scores (top " + limit + ") ===");
        List<HighScore> scores = repo.highestScores(limit);
        if (scores.isEmpty()) {
            System.out.println("(no scores recorded yet)");
            return;
        }
        for (HighScore s : scores) {
            System.out.printf("%-12s %4d  (%s)%n",
                    s.player(), s.points(),
                    s.achievedAt() != null ? TS.format(s.achievedAt()) : "-");
        }
    }
}
