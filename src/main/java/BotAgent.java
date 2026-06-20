import java.util.List;

/** Bot decision-making, delegating card/colour choice to {@link BotAI}. */
public class BotAgent implements PlayerAgent {

    private final BotAI botAI;

    public BotAgent(BotAI botAI) {
        this.botAI = botAI;
    }

    @Override
    public int chooseCardIndex(GameState state, List<String> hand) {
        return botAI.chooseBotCard(state, hand);
    }

    @Override
    public boolean playDrawnCard(GameState state, List<String> hand, String drawnCard) {
        // Bots always play a freshly drawn card when it is legal.
        return true;
    }

    @Override
    public String chooseWildColor(GameState state, List<String> hand) {
        return botAI.chooseBotColor(hand);
    }

    @Override
    public boolean callUno(GameState state, List<String> hand) {
        // Bots reliably remember to call UNO.
        return true;
    }
}
