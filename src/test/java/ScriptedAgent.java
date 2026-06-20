import java.util.List;

/**
 * Deterministic test agent: plays the first legal card in hand, otherwise
 * draws. UNO calls, wild colour, and whether to play a drawn card are
 * configurable so individual behaviours can be exercised.
 */
class ScriptedAgent implements PlayerAgent {

    private final GameRules rules;
    boolean callsUno = true;
    boolean playsDrawnCard = true;
    String wildColor = "R";

    ScriptedAgent(GameRules rules) {
        this.rules = rules;
    }

    @Override
    public int chooseCardIndex(GameState state, List<String> hand) {
        for (int i = 0; i < hand.size(); i++) {
            if (rules.canPlay(hand.get(i), state.upCard, state.calledColor)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean playDrawnCard(GameState state, List<String> hand, String drawnCard) {
        return playsDrawnCard;
    }

    @Override
    public String chooseWildColor(GameState state, List<String> hand) {
        return wildColor;
    }

    @Override
    public boolean callUno(GameState state, List<String> hand) {
        return callsUno;
    }
}
