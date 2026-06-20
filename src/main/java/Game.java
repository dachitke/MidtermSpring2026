import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import persistence.GameResult;
import persistence.RoundResult;

/**
 * CLI adapter: wires up players (human/bots), the console view, and the match
 * controller, then maps the result into a persistable {@link GameResult}.
 *
 * <p>All game rules live in {@link RoundController} / {@link MatchController} /
 * {@link GameRules} / {@link GameEngine}; this class only assembles them and
 * connects the console.
 */
public class Game {

    private final GameState state = new GameState();
    private final GameRules rules = new GameRules();
    private final BotAI botAI = new BotAI(rules);
    private final Scanner scanner = new Scanner(System.in);

    private final MatchController match;
    private final boolean valid;

    public Game(int bots, boolean human, long seed, int targetScore) {
        Random random = new Random(seed);
        this.valid = setupPlayers(bots, human);

        GameEngine engine = new GameEngine(state, rules, random);

        List<PlayerAgent> agents = new ArrayList<>();
        for (int i = 0; i < state.playerNames.size(); i++) {
            agents.add(state.humanPlayers.get(i)
                    ? new HumanAgent(rules, scanner)
                    : new BotAgent(botAI));
        }

        GameListener listener = new ConsoleGameListener();
        RoundController round = new RoundController(state, rules, engine, random, agents, listener);
        this.match = new MatchController(state, round, listener, random, targetScore);
    }

    /** Plays the full match and returns its persistable result (or null if setup was invalid). */
    public GameResult playGame() {
        if (!valid) {
            System.out.println("Need between 2 and 4 players to start a game.");
            return null;
        }
        return toGameResult(match.run());
    }

    private boolean setupPlayers(int bots, boolean human) {
        int players = bots + (human ? 1 : 0);
        if (players < 2 || players > 4) {
            return false;
        }

        state.playerNames.clear();
        state.humanPlayers.clear();
        state.hands.clear();

        if (human) {
            addPlayer("You", true);
        }
        for (int i = 1; i <= bots; i++) {
            addPlayer("Bot" + i, false);
        }
        return true;
    }

    private void addPlayer(String name, boolean isHuman) {
        state.playerNames.add(name);
        state.humanPlayers.add(isHuman);
        state.hands.add(new ArrayList<>());
    }

    private GameResult toGameResult(MatchOutcome outcome) {
        List<String> names = outcome.playerNames();
        String winnerName = outcome.finalWinner() >= 0 ? names.get(outcome.finalWinner()) : null;

        List<RoundResult> rounds = new ArrayList<>();
        for (RoundRecord r : outcome.rounds()) {
            String roundWinner = r.winner() >= 0 ? names.get(r.winner()) : null;

            Map<String, Integer> scores = new LinkedHashMap<>();
            for (String name : names) {
                scores.put(name, 0);
            }
            if (roundWinner != null) {
                scores.put(roundWinner, r.points());
            }
            rounds.add(new RoundResult(r.roundNumber(), roundWinner, scores));
        }

        return new GameResult(outcome.startedAt(), outcome.endedAt(), names, winnerName, rounds);
    }
}
