import java.util.List;

/**
 * A player's decision-making, deliberately free of any console I/O so that game
 * flow can be driven and tested without a terminal. Bots, humans, and scripted
 * test players all implement this.
 */
public interface PlayerAgent {

    /**
     * Index of the card in {@code hand} to play, or {@code -1} to draw.
     * Implementations should only return the index of a legally playable card.
     */
    int chooseCardIndex(GameState state, List<String> hand);

    /** After drawing a legal card, whether to play it now (true) or pass. */
    boolean playDrawnCard(GameState state, List<String> hand, String drawnCard);

    /** Colour (R/Y/G/B) to set after playing a Wild or Wild Draw Four. */
    String chooseWildColor(GameState state, List<String> hand);

    /** Whether the player calls "UNO" upon reaching a single card. */
    boolean callUno(GameState state, List<String> hand);
}
