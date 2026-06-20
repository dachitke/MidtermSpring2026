import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Result of a full multi-round match: final winner, totals, and per-round records. */
public final class MatchOutcome {

    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final List<String> playerNames;
    private final int finalWinner;
    private final int[] totals;
    private final List<RoundRecord> rounds;

    public MatchOutcome(LocalDateTime startedAt, LocalDateTime endedAt, List<String> playerNames,
                        int finalWinner, int[] totals, List<RoundRecord> rounds) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.playerNames = new ArrayList<>(playerNames);
        this.finalWinner = finalWinner;
        this.totals = totals.clone();
        this.rounds = new ArrayList<>(rounds);
    }

    public LocalDateTime startedAt() {
        return startedAt;
    }

    public LocalDateTime endedAt() {
        return endedAt;
    }

    public List<String> playerNames() {
        return playerNames;
    }

    /** Index of the final winner, or {@code -1} if none reached the target. */
    public int finalWinner() {
        return finalWinner;
    }

    public int[] totals() {
        return totals.clone();
    }

    public List<RoundRecord> rounds() {
        return rounds;
    }
}
