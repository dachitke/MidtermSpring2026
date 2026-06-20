package persistence;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain result of a finished game, produced by the game engine and handed to
 * the persistence layer. It deliberately contains no JPA or SQL concerns so
 * that game logic stays independent of persistence.
 */
public class GameResult {

    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final List<String> playerNames;
    private final String winnerName;
    private final List<RoundResult> rounds;

    public GameResult(LocalDateTime startedAt, LocalDateTime endedAt, List<String> playerNames,
                      String winnerName, List<RoundResult> rounds) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.playerNames = new ArrayList<>(playerNames);
        this.winnerName = winnerName;
        this.rounds = new ArrayList<>(rounds);
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    /** Winner of the game, or {@code null} if the game ended without one. */
    public String getWinnerName() {
        return winnerName;
    }

    public List<RoundResult> getRounds() {
        return rounds;
    }
}
