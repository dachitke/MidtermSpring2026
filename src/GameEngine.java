import java.util.Collections;
import java.util.Random;

public class GameEngine {

    GameState state;
    GameRules rules;
    Random random;

    public GameEngine(GameState state, GameRules rules, Random random) {
        this.state = state;
        this.rules = rules;
        this.random = random;
    }
    void next() {
        state.currentPlayer += state.direction;

        if (state.currentPlayer >= state.playerNames.size()) state.currentPlayer = 0;
        if (state.currentPlayer < 0) state.currentPlayer = state.playerNames.size() - 1;
    }
    String draw() {

        if (state.deck.isEmpty()) {
            state.deck.addAll(state.discard);
            state.discard.clear();
            Collections.shuffle(state.deck, random);
        }

        if (state.deck.isEmpty()) return "W";

        return state.deck.remove(0);
    }
    void giveCardToCurrentPlayer(int count) {
        for (int i = 0; i < count; i++) {
            state.hands.get(state.currentPlayer).add(draw());
        }
    }
    void applyCardEffect(String card) {

        String rank = rules.rank(card);

        switch (rank) {
            case "SKIP" -> {
                next();
                next();
            }
            case "REVERSE" -> {
                state.direction *= -1;
                next();
            }
            case "DRAW_TWO" -> {

                next();
                giveCardToCurrentPlayer(2);
                next();
            }
            case "WILD_DRAW_FOUR" -> {

                next();
                giveCardToCurrentPlayer(4);
                next();
            }
            default -> next();
        }
    }

}