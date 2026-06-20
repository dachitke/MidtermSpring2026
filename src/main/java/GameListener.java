import java.util.List;

/**
 * Receives game events for presentation (CLI rendering, etc.). All methods have
 * empty defaults so game logic can run "headless" in tests by supplying an
 * implementation that overrides nothing.
 */
public interface GameListener {

    default void gameStarted(List<String> playerNames, int targetScore) {
    }

    default void roundStarted(int roundNumber, int firstPlayer, String upCard) {
    }

    default void turnStarted(int player, String name, List<String> hand, String upCard, String calledColor) {
    }

    default void cardPlayed(int player, String name, String card) {
    }

    default void colorChosen(int player, String name, String color) {
    }

    default void cardDrawn(int player, String name, String card) {
    }

    default void passed(int player, String name) {
    }

    default void illegalPlay(int player, String name, String card) {
    }

    default void unoCalled(int player, String name) {
    }

    default void missedUno(int player, String name) {
    }

    default void roundEnded(int winner, String name, int points, int[] totals, List<String> playerNames) {
    }

    default void gameEnded(int finalWinner, String name, int[] totals, List<String> playerNames) {
    }
}
