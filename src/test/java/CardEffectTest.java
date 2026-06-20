import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Action-card effects applied through {@link GameEngine}. */
class CardEffectTest {

    private GameState stateWith(int players, String... deck) {
        GameState state = new GameState();
        for (int i = 0; i < players; i++) {
            state.playerNames.add("P" + i);
            state.hands.add(new ArrayList<>());
        }
        state.deck.addAll(Arrays.asList(deck));
        state.currentPlayer = 0;
        state.direction = 1;
        return state;
    }

    private GameEngine engine(GameState state) {
        return new GameEngine(state, new GameRules(), new Random(1));
    }

    @Test
    void skipMovesPastNextPlayer() {
        GameState state = stateWith(3);
        engine(state).applyCardEffect("RS");
        assertEquals(2, state.currentPlayer);
    }

    @Test
    void reverseChangesDirectionWithThreePlayers() {
        GameState state = stateWith(3);
        engine(state).applyCardEffect("RR");
        assertEquals(-1, state.direction);
        assertEquals(2, state.currentPlayer); // 0 going backwards wraps to 2
    }

    @Test
    void reverseActsLikeSkipWithTwoPlayers() {
        GameState state = stateWith(2);
        engine(state).applyCardEffect("RR");
        assertEquals(0, state.currentPlayer); // same player plays again
    }

    @Test
    void drawTwoGivesTwoCardsAndSkipsNextPlayer() {
        GameState state = stateWith(3, "B1", "B2");
        engine(state).applyCardEffect("R+2");
        assertEquals(2, state.hands.get(1).size()); // player 1 drew two
        assertEquals(2, state.currentPlayer);        // and lost their turn
    }

    @Test
    void wildDrawFourGivesFourCardsAndSkipsNextPlayer() {
        GameState state = stateWith(3, "B1", "B2", "B3", "B4");
        engine(state).applyCardEffect("W4");
        assertEquals(4, state.hands.get(1).size()); // player 1 drew four
        assertEquals(2, state.currentPlayer);        // and lost their turn
    }
}
