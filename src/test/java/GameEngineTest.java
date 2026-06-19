import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {

    @Test
    void skipCardShouldSkipNextPlayer() {

        GameState state = new GameState();
        GameRules rules = new GameRules();
        GameEngine engine = new GameEngine(state, rules, new Random(1));

        // setup players
        state.playerNames = new ArrayList<>();
        state.playerNames.add("A");
        state.playerNames.add("B");
        state.playerNames.add("C");

        state.hands = new ArrayList<>();
        state.hands.add(new ArrayList<>());
        state.hands.add(new ArrayList<>());
        state.hands.add(new ArrayList<>());

        state.currentPlayer = 0;
        state.direction = 1;

        // play skip card
        engine.applyCardEffect("R S"); // assuming SKIP is "S"

        // expected: 0 -> 2 (skips player 1)
        assertEquals(2, state.currentPlayer);
    }
}