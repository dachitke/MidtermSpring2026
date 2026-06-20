import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundControllerTest {

    private final GameRules rules = new GameRules();

    private static class Fixture {
        GameState state = new GameState();
        RecordingListener listener = new RecordingListener();
        List<PlayerAgent> agents = new ArrayList<>();
        RoundController controller;
    }

    private Fixture twoPlayerFixture(String upCard, List<String> p0, List<String> p1, String... deck) {
        Fixture f = new Fixture();
        f.state.playerNames.addAll(Arrays.asList("P0", "P1"));
        f.state.hands.add(new ArrayList<>(p0));
        f.state.hands.add(new ArrayList<>(p1));
        f.state.deck.addAll(Arrays.asList(deck));
        f.state.upCard = upCard;
        f.state.calledColor = "";
        f.state.currentPlayer = 0;
        f.state.direction = 1;
        f.agents.add(new ScriptedAgent(rules));
        f.agents.add(new ScriptedAgent(rules));
        GameEngine engine = new GameEngine(f.state, rules, new Random(1));
        f.controller = new RoundController(f.state, rules, engine, new Random(1), f.agents, f.listener);
        return f;
    }

    @Test
    void scoreOthersSumsAllOpponentCardValues() {
        Fixture f = twoPlayerFixture("R3", List.of(), List.of("R5", "B+2", "W"));
        // winner = player 0; opponent holds 5 + 20 + 50
        assertEquals(75, f.controller.scoreOthers(0));
    }

    @Test
    void playerDrawsThenPassesWhenNoLegalPlay() {
        Fixture f = twoPlayerFixture("R3", List.of("B5"), List.of("R7"), "G2");

        RoundOutcome outcome = f.controller.runLoop(1);

        assertTrue(f.listener.has("DRAW:0:G2"));
        assertTrue(f.listener.has("PASS:0"));
        assertEquals(1, outcome.winner());     // P1 played R7 and won
        assertEquals(7, outcome.points());     // P0 left holding B5 + G2 = 7
    }

    @Test
    void callingUnoAvoidsPenalty() {
        Fixture f = twoPlayerFixture("R3", new ArrayList<>(List.of("R5", "R6")), List.of("R7"), "B1", "B2");
        ((ScriptedAgent) f.agents.get(0)).callsUno = true;

        f.controller.runLoop(1);

        assertTrue(f.listener.has("UNO:0"));
        assertFalse(f.listener.has("MISSED_UNO:0"));
        assertEquals(1, f.state.hands.get(0).size()); // kept its single card
    }

    @Test
    void missingUnoDrawsTwoPenaltyCards() {
        Fixture f = twoPlayerFixture("R3", new ArrayList<>(List.of("R5", "R6")), List.of("R7"), "B1", "B2");
        ((ScriptedAgent) f.agents.get(0)).callsUno = false;

        f.controller.runLoop(1);

        assertTrue(f.listener.has("MISSED_UNO:0"));
        // started with 2, played 1 (-> 1), drew 2 penalty (-> 3)
        assertEquals(3, f.state.hands.get(0).size());
    }

    @Test
    void wildSetsChosenColor() {
        Fixture f = twoPlayerFixture("R3", new ArrayList<>(List.of("W", "B1")), List.of("G7"));
        ((ScriptedAgent) f.agents.get(0)).wildColor = "G";

        f.controller.runLoop(1);

        assertTrue(f.listener.has("COLOR:0:G"));
    }
}
