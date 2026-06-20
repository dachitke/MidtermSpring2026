import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchControllerTest {

    @Test
    void matchRunsRoundsUntilTargetReachedAndPicksWinner() {
        GameState state = new GameState();
        state.playerNames.addAll(List.of("A", "B"));
        state.hands.add(new ArrayList<>());
        state.hands.add(new ArrayList<>());

        GameRules rules = new GameRules();
        Random rng = new Random(42);
        GameEngine engine = new GameEngine(state, rules, rng);

        List<PlayerAgent> agents = List.of(new BotAgent(new BotAI(rules)), new BotAgent(new BotAI(rules)));
        GameListener silent = new GameListener() {
        };

        RoundController round = new RoundController(state, rules, engine, rng, agents, silent);
        int target = 50;
        MatchController match = new MatchController(state, round, silent, rng, target);

        MatchOutcome outcome = match.run();

        assertFalse(outcome.rounds().isEmpty());
        assertTrue(outcome.finalWinner() >= 0, "a final winner should be chosen");
        assertTrue(outcome.totals()[outcome.finalWinner()] >= target,
                "winner should have reached the target score");
    }
}
