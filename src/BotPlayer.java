import java.util.ArrayList;

public class BotPlayer extends Player {

    private final GameRules gameRules = new GameRules();

    public BotPlayer(String name) {
        super(name);
    }

//    public int chooseMove(ArrayList<String> hand, String upCard) {
//        for (int i = 0; i < hand.size(); i++) {
//            if (gameRules.canPlay(hand.get(i), upCard,called)) {
//                return i;
//            }
//        }
//
//        return -1;
//    }
}