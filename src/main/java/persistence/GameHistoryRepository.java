package persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import persistence.reports.HighScore;
import persistence.reports.RecentGame;
import persistence.reports.WinCount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data-access object for persisted game history. All database access goes
 * through here using JPA; callers (game flow, CLI) never touch SQL.
 */
public class GameHistoryRepository {

    private final EntityManagerFactory emf;

    public GameHistoryRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /** Persists a finished game (players, rounds, scores, winner, timestamps). */
    public Long save(GameResult result) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Map<String, Player> players = new HashMap<>();
            for (String name : result.getPlayerNames()) {
                players.put(name, findOrCreatePlayer(em, name));
            }

            GameRecord game = new GameRecord(
                    result.getStartedAt(),
                    result.getEndedAt(),
                    result.getWinnerName() == null ? null : players.get(result.getWinnerName()),
                    result.getRounds().size());

            for (RoundResult roundResult : result.getRounds()) {
                Player roundWinner = roundResult.getWinnerName() == null
                        ? null : players.get(roundResult.getWinnerName());
                Round round = new Round(roundResult.getRoundNumber(), roundWinner);

                for (Map.Entry<String, Integer> entry : roundResult.getScoresByPlayer().entrySet()) {
                    Player player = players.computeIfAbsent(entry.getKey(), n -> findOrCreatePlayer(em, n));
                    round.addScore(new Score(player, entry.getValue()));
                }
                game.addRound(round);
            }

            em.persist(game);
            tx.commit();
            return game.getId();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private Player findOrCreatePlayer(EntityManager em, String name) {
        List<Player> found = em.createQuery("select p from Player p where p.name = :name", Player.class)
                .setParameter("name", name)
                .getResultList();
        if (!found.isEmpty()) {
            return found.get(0);
        }
        Player player = new Player(name);
        em.persist(player);
        return player;
    }

    /** Most recently started games first. */
    public List<RecentGame> recentGames(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<RecentGame> query = em.createQuery(
                    "select new persistence.reports.RecentGame("
                            + "g.id, g.startedAt, g.endedAt, "
                            + "coalesce(w.name, 'none'), g.roundsPlayed) "
                            + "from GameRecord g left join g.winner w "
                            + "order by g.startedAt desc, g.id desc",
                    RecentGame.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /** Number of games won, per player, highest first. */
    public List<WinCount> winCounts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "select new persistence.reports.WinCount(p.name, count(g)) "
                            + "from GameRecord g join g.winner p "
                            + "group by p.name order by count(g) desc, p.name asc",
                    WinCount.class).getResultList();
        } finally {
            em.close();
        }
    }

    /** Highest single-round scores across all games, highest first. */
    public List<HighScore> highestScores(int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<HighScore> query = em.createQuery(
                    "select new persistence.reports.HighScore("
                            + "s.player.name, s.points, s.round.game.startedAt) "
                            + "from Score s order by s.points desc, s.player.name asc",
                    HighScore.class);
            query.setMaxResults(limit);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
