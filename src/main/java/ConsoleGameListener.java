import java.util.List;

/** Renders game events to the console for players. Contains no game rules. */
public class ConsoleGameListener implements GameListener {

    @Override
    public void gameStarted(List<String> playerNames, int targetScore) {
        System.out.println("Players: " + String.join(", ", playerNames));
        System.out.println("Playing to " + targetScore + " points.");
    }

    @Override
    public void roundStarted(int roundNumber, int firstPlayer, String upCard) {
        System.out.println("\n--- Round " + roundNumber + " --- (starting up card: " + upCard + ")");
    }

    @Override
    public void turnStarted(int player, String name, List<String> hand, String upCard, String calledColor) {
        System.out.println("\nUp card: " + upCard + (calledColor.isEmpty() ? "" : " called " + calledColor));
        System.out.println(name + " hand: " + join(hand));
    }

    @Override
    public void cardPlayed(int player, String name, String card) {
        System.out.println(name + " plays " + card);
    }

    @Override
    public void colorChosen(int player, String name, String color) {
        System.out.println(name + " calls " + color);
    }

    @Override
    public void cardDrawn(int player, String name, String card) {
        System.out.println(name + " draws " + card);
    }

    @Override
    public void passed(int player, String name) {
        System.out.println(name + " passes");
    }

    @Override
    public void illegalPlay(int player, String name, String card) {
        System.out.println(name + " tried an illegal card " + card + " and draws a penalty.");
    }

    @Override
    public void unoCalled(int player, String name) {
        System.out.println(name + " says UNO!");
    }

    @Override
    public void missedUno(int player, String name) {
        System.out.println(name + " forgot to call UNO and draws 2 penalty cards.");
    }

    @Override
    public void roundEnded(int winner, String name, int points, int[] totals, List<String> playerNames) {
        System.out.println(name + " wins the round and scores " + points + ".");
        System.out.println("Totals: " + scoreLine(totals, playerNames));
    }

    @Override
    public void gameEnded(int finalWinner, String name, int[] totals, List<String> playerNames) {
        System.out.println("\n=== " + name + " wins the game! ===");
        System.out.println("Final totals: " + scoreLine(totals, playerNames));
    }

    private String scoreLine(int[] totals, List<String> playerNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerNames.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(playerNames.get(i)).append('=').append(totals[i]);
        }
        return sb.toString();
    }

    private String join(List<String> cards) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(i).append(':').append(cards.get(i));
            if (i < cards.size() - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
