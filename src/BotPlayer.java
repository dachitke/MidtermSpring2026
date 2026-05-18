public class BotPlayer extends Player {

    private final GameRules gameRules = new GameRules();

    public BotPlayer(String name) {
        super(name);
    }

    public int chooseMove(Card upCard) {
        for (int i = 0; i < hand.size(); i++) {
            if (gameRules.canPlay(hand.get(i), upCard)) {
                return i;
            }
        }

        return -1;
    }
}