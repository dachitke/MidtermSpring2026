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
}