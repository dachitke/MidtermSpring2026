package persistence;

import java.util.LinkedHashMap;
import java.util.Map;

/** Result of a single round: its winner and each player's score. */
public class RoundResult {

    private final int roundNumber;
    private final String winnerName;
    private final Map<String, Integer> scoresByPlayer;

    public RoundResult(int roundNumber, String winnerName, Map<String, Integer> scoresByPlayer) {
        this.roundNumber = roundNumber;
        this.winnerName = winnerName;
        this.scoresByPlayer = new LinkedHashMap<>(scoresByPlayer);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public Map<String, Integer> getScoresByPlayer() {
        return scoresByPlayer;
    }
}
