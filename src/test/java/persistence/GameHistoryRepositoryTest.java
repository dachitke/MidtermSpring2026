package persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import persistence.reports.HighScore;
import persistence.reports.RecentGame;
import persistence.reports.WinCount;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Persistence-layer tests. Each test runs against its own throwaway in-memory
 * H2 database (create-drop), so they never touch a developer's machine state
 * or the file-based runtime database.
 */
class GameHistoryRepositoryTest {

    private EntityManagerFactory emf;
    private GameHistoryRepository repo;

    @BeforeEach
    void setUp() {
        Map<String, String> overrides = new LinkedHashMap<>();
        // Unique name per test => full isolation between tests.
        overrides.put("jakarta.persistence.jdbc.url",
                "jdbc:h2:mem:uno_test_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        overrides.put("hibernate.hbm2ddl.auto", "create-drop");
        emf = Persistence.createEntityManagerFactory(PersistenceManager.PERSISTENCE_UNIT, overrides);
        repo = new GameHistoryRepository(emf);
    }

    @AfterEach
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private GameResult game(String winner, LocalDateTime when, Map<String, Integer> scores) {
        RoundResult round = new RoundResult(1, winner, scores);
        return new GameResult(when, when.plusMinutes(1), List.copyOf(scores.keySet()),
                winner, List.of(round));
    }

    private Map<String, Integer> scores(String winner, int winnerPoints, String loser) {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put(winner, winnerPoints);
        m.put(loser, 0);
        return m;
    }

    @Test
    void saveReturnsGeneratedIdAndPersistsGame() {
        Long id = repo.save(game("Alice", LocalDateTime.now(), scores("Alice", 30, "Bob")));

        assertNotNull(id);
        List<RecentGame> recent = repo.recentGames(10);
        assertEquals(1, recent.size());
        assertEquals("Alice", recent.get(0).winner());
        assertEquals(1, recent.get(0).roundsPlayed());
    }

    @Test
    void recentGamesAreOrderedNewestFirst() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 10, 0);
        repo.save(game("Alice", base, scores("Alice", 10, "Bob")));
        repo.save(game("Bob", base.plusHours(1), scores("Bob", 20, "Alice")));
        repo.save(game("Alice", base.plusHours(2), scores("Alice", 40, "Bob")));

        List<RecentGame> recent = repo.recentGames(2);

        assertEquals(2, recent.size());
        assertEquals(base.plusHours(2), recent.get(0).startedAt());
        assertEquals(base.plusHours(1), recent.get(1).startedAt());
    }

    @Test
    void winCountsAggregatePerPlayerAndReuseExistingPlayers() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 10, 0);
        repo.save(game("Alice", base, scores("Alice", 10, "Bob")));
        repo.save(game("Alice", base.plusHours(1), scores("Alice", 15, "Bob")));
        repo.save(game("Bob", base.plusHours(2), scores("Bob", 25, "Alice")));

        List<WinCount> wins = repo.winCounts();

        assertEquals(2, wins.size());
        assertEquals("Alice", wins.get(0).player());
        assertEquals(2, wins.get(0).wins());
        assertEquals("Bob", wins.get(1).player());
        assertEquals(1, wins.get(1).wins());
    }

    @Test
    void highestScoresAreOrderedByPointsDescending() {
        LocalDateTime base = LocalDateTime.of(2026, 1, 1, 10, 0);
        repo.save(game("Alice", base, scores("Alice", 30, "Bob")));
        repo.save(game("Bob", base.plusHours(1), scores("Bob", 99, "Alice")));

        List<HighScore> top = repo.highestScores(3);

        assertEquals("Bob", top.get(0).player());
        assertEquals(99, top.get(0).points());
        assertTrue(top.get(0).points() >= top.get(1).points());
    }

    @Test
    void gameWithoutWinnerIsStillPersisted() {
        Map<String, Integer> noWinnerScores = new LinkedHashMap<>();
        noWinnerScores.put("Alice", 0);
        noWinnerScores.put("Bob", 0);

        Long id = repo.save(new GameResult(LocalDateTime.now(), LocalDateTime.now(),
                List.of("Alice", "Bob"), null, List.of(new RoundResult(1, null, noWinnerScores))));

        assertNotNull(id);
        assertEquals(1, repo.recentGames(10).size());
        assertTrue(repo.winCounts().isEmpty());
    }
}
