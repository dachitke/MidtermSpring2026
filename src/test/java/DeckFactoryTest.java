import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckFactoryTest {

    private final List<String> deck = DeckFactory.standardDeck();

    private long count(String card) {
        return deck.stream().filter(c -> c.equals(card)).count();
    }

    @Test
    void deckHas108Cards() {
        assertEquals(108, deck.size());
    }

    @Test
    void oneZeroAndTwoOfEachNumberPerColor() {
        for (String c : DeckFactory.COLORS) {
            assertEquals(1, count(c + "0"), "one zero for " + c);
            for (int n = 1; n <= 9; n++) {
                assertEquals(2, count(c + n), "two " + n + " for " + c);
            }
        }
    }

    @Test
    void twoOfEachActionCardPerColor() {
        for (String c : DeckFactory.COLORS) {
            assertEquals(2, count(c + "S"), "two Skip for " + c);
            assertEquals(2, count(c + "R"), "two Reverse for " + c);
            assertEquals(2, count(c + "+2"), "two Draw Two for " + c);
        }
    }

    @Test
    void fourWildAndFourWildDrawFour() {
        assertEquals(4, count("W"));
        assertEquals(4, count("W4"));
    }
}
