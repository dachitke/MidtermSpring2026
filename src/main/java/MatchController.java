import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plays multiple rounds until a player reaches the target score, accumulating
 * scores and determining the overall winner.
 */
public class MatchController {

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    private static final int MAX_ROUNDS = 1000;

    private final GameState state;
    private final RoundController roundController;
    private final GameListener listener;
    private final Random random;
    private final int targetScore;

    public MatchController(GameState state, RoundController roundController, GameListener listener,
                           Random random, int targetScore) {
        this.state = state;
        this.roundController = roundController;
        this.listener = listener;
        this.random = random;
        this.targetScore = targetScore;
    }

    public MatchOutcome run() {
        LocalDateTime startedAt = LocalDateTime.now();
        int n = state.playerNames.size();

        listener.gameStarted(state.playerNames, targetScore);
        log.info("Match start: players={}, target={}", state.playerNames, targetScore);

        List<RoundRecord> records = new ArrayList<>();
        int firstPlayer = random.nextInt(n);
        int roundNumber = 1;

        while (highestScore() < targetScore && roundNumber <= MAX_ROUNDS) {
            RoundOutcome outcome = roundController.play(roundNumber, firstPlayer);
            records.add(new RoundRecord(roundNumber, outcome.winner(), outcome.points()));

            // The round winner leads the next round; otherwise rotate.
            firstPlayer = outcome.hasWinner() ? outcome.winner() : (firstPlayer + 1) % n;
            roundNumber++;
        }

        int finalWinner = highestScore() >= targetScore ? argMaxScore() : -1;
        LocalDateTime endedAt = LocalDateTime.now();

        if (finalWinner >= 0) {
            log.info("Match end: winner={}, totals={}",
                    state.playerNames.get(finalWinner), java.util.Arrays.toString(snapshotTotals()));
            listener.gameEnded(finalWinner, state.playerNames.get(finalWinner), state.scores, state.playerNames);
        } else {
            log.warn("Match end: no winner reached target {}", targetScore);
        }

        return new MatchOutcome(startedAt, endedAt, state.playerNames, finalWinner, snapshotTotals(), records);
    }

    private int highestScore() {
        int best = 0;
        for (int i = 0; i < state.playerNames.size(); i++) {
            best = Math.max(best, state.scores[i]);
        }
        return best;
    }

    private int argMaxScore() {
        int best = 0;
        for (int i = 1; i < state.playerNames.size(); i++) {
            if (state.scores[i] > state.scores[best]) {
                best = i;
            }
        }
        return best;
    }

    private int[] snapshotTotals() {
        int n = state.playerNames.size();
        int[] totals = new int[n];
        System.arraycopy(state.scores, 0, totals, 0, n);
        return totals;
    }
}
