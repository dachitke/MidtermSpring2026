import java.util.ArrayList;
import java.util.List;

/**
 * Builds a standard 108-card UNO deck. Kept separate from game flow so deck
 * composition can be verified without running the CLI.
 *
 * <p>Card encoding: a colour letter (R/Y/G/B) followed by a rank — a digit
 * {@code 0-9}, {@code S} (Skip), {@code R} (Reverse) or {@code +2} (Draw Two).
 * Wilds are {@code W} and Wild Draw Four is {@code W4}.
 */
public final class DeckFactory {

    public static final String[] COLORS = {"R", "Y", "G", "B"};

    private DeckFactory() {
    }

    /** A fresh, unshuffled 108-card deck. */
    public static List<String> standardDeck() {
        List<String> deck = new ArrayList<>(108);

        for (String c : COLORS) {
            deck.add(c + "0");                 // one 0 per colour

            for (int n = 1; n <= 9; n++) {     // two of each 1-9 per colour
                deck.add(c + n);
                deck.add(c + n);
            }

            deck.add(c + "S");                 // two Skip per colour
            deck.add(c + "S");
            deck.add(c + "R");                 // two Reverse per colour
            deck.add(c + "R");
            deck.add(c + "+2");                // two Draw Two per colour
            deck.add(c + "+2");
        }

        for (int i = 0; i < 4; i++) {          // four Wild, four Wild Draw Four
            deck.add("W");
            deck.add("W4");
        }

        return deck;
    }
}
