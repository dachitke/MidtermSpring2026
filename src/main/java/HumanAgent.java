import java.util.List;
import java.util.Scanner;

/**
 * Console-driven human player. This is the only decision class that touches
 * {@code System.in}/{@code System.out}; all game rules live elsewhere.
 */
public class HumanAgent implements PlayerAgent {

    private final GameRules rules;
    private final Scanner scanner;

    public HumanAgent(GameRules rules, Scanner scanner) {
        this.rules = rules;
        this.scanner = scanner;
    }

    @Override
    public int chooseCardIndex(GameState state, List<String> hand) {
        while (true) {
            System.out.print("Choose index or DRAW: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("DRAW")) {
                return -1;
            }

            try {
                int idx = Integer.parseInt(input);
                if (idx >= 0 && idx < hand.size()
                        && rules.canPlay(hand.get(idx), state.upCard, state.calledColor)) {
                    return idx;
                }
            } catch (NumberFormatException ignored) {
                // fall through to card-name matching
            }

            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)
                        && rules.canPlay(hand.get(i), state.upCard, state.calledColor)) {
                    return i;
                }
            }

            System.out.println("Invalid move. Enter a playable index/card, or DRAW.");
        }
    }

    @Override
    public boolean playDrawnCard(GameState state, List<String> hand, String drawnCard) {
        System.out.print("Play drawn card " + drawnCard + "? y/n: ");
        String answer = scanner.nextLine().trim();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }

    @Override
    public String chooseWildColor(GameState state, List<String> hand) {
        while (true) {
            System.out.print("Choose color R/Y/G/B: ");
            String c = scanner.nextLine().trim().toUpperCase();
            if (c.matches("[RYGB]")) {
                return c;
            }
            System.out.println("Invalid color.");
        }
    }

    @Override
    public boolean callUno(GameState state, List<String> hand) {
        System.out.print("You are down to one card. Call UNO? y/n: ");
        String answer = scanner.nextLine().trim();
        return answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes");
    }
}
