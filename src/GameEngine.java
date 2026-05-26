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
    void applyCardEffect(String card) {

        String rank = rules.rank(card);

        if (rank.equals("SKIP")) {
            next();
            next();

        } else if (rank.equals("REVERSE")) {
            state.direction *= -1;
            next();

        } else if (rank.equals("DRAW_TWO")) {

            next();

            state.hands.get(state.currentPlayer).add(draw());
            state.hands.get(state.currentPlayer).add(draw());

            next();

        } else if (rank.equals("WILD_DRAW_FOUR")) {

            next();

            for (int i = 0; i < 4; i++) {
                state.hands.get(state.currentPlayer).add(draw());
            }

            next();

        } else {
            next();
        }
    }
}