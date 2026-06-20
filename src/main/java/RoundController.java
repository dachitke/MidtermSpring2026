import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs a single UNO round to completion. It owns the turn loop, legal-play
 * validation, draw/pass flow, action-card effects, the UNO call / missed-UNO
 * penalty, and round scoring.
 *
 * <p>It depends only on {@link PlayerAgent}s and a {@link GameListener}, never
 * on the console, so rounds can be driven deterministically in tests.
 */
public class RoundController {

    private static final Logger log = LoggerFactory.getLogger(RoundController.class);

    private static final int SAFETY_LIMIT = 5000;

    private final GameState state;
    private final GameRules rules;
    private final GameEngine engine;
    private final Random random;
    private final List<PlayerAgent> agents;
    private final GameListener listener;

    public RoundController(GameState state, GameRules rules, GameEngine engine, Random random,
                           List<PlayerAgent> agents, GameListener listener) {
        this.state = state;
        this.rules = rules;
        this.engine = engine;
        this.random = random;
        this.agents = agents;
        this.listener = listener;
    }

    /** Deals a fresh round starting with {@code firstPlayer} and plays it out. */
    public RoundOutcome play(int roundNumber, int firstPlayer) {
        deal();
        state.currentPlayer = firstPlayer;
        state.direction = 1;

        log.info("Round {} start: players={}, first={}, upCard={}",
                roundNumber, state.playerNames, state.playerNames.get(firstPlayer), state.upCard);
        listener.roundStarted(roundNumber, firstPlayer, state.upCard);

        return runLoop(roundNumber);
    }

    /**
     * Runs the turn loop on an already set-up {@link GameState} (hands, deck,
     * up card, current player). Exposed for deterministic testing.
     */
    RoundOutcome runLoop(int roundNumber) {
        int guard = 0;
        while (guard++ < SAFETY_LIMIT) {
            int i = state.currentPlayer;
            String name = state.playerNames.get(i);
            List<String> hand = state.hands.get(i);
            PlayerAgent agent = agents.get(i);

            listener.turnStarted(i, name, hand, state.upCard, state.calledColor);
            log.info("Turn: player={}, handSize={}, upCard={}, calledColor={}",
                    name, hand.size(), state.upCard, state.calledColor.isEmpty() ? "-" : state.calledColor);

            int chosen = agent.chooseCardIndex(state, hand);

            if (chosen < 0) {
                String drawn = engine.draw();
                hand.add(drawn);
                listener.cardDrawn(i, name, drawn);
                log.info("Card drawn: player={}, card={}, handSize={}", name, drawn, hand.size());

                if (rules.canPlay(drawn, state.upCard, state.calledColor)
                        && agent.playDrawnCard(state, hand, drawn)) {
                    chosen = hand.size() - 1;
                } else {
                    listener.passed(i, name);
                    log.info("Pass: player={}", name);
                    engine.next();
                    continue;
                }
            }

            String card = hand.get(chosen);
            if (chosen >= hand.size() || !rules.canPlay(card, state.upCard, state.calledColor)) {
                listener.illegalPlay(i, name, card);
                log.info("Invalid input: player={} illegal card {} on upCard={}", name, card, state.upCard);
                hand.add(engine.draw());
                engine.next();
                continue;
            }

            playChosenCard(i, name, hand, chosen, card);

            if (hand.isEmpty()) {
                int points = scoreOthers(i);
                state.scores[i] += points;
                log.info("Round {} end: winner={}, points={}", roundNumber, name, points);
                listener.roundEnded(i, name, points, state.scores, state.playerNames);
                return new RoundOutcome(i, points);
            }

            engine.applyCardEffect(card);
        }

        log.warn("Round {} stopped at safety limit", roundNumber);
        return new RoundOutcome(-1, 0);
    }

    private void playChosenCard(int i, String name, List<String> hand, int chosen, String card) {
        hand.remove(chosen);
        state.discard.add(state.upCard);
        state.upCard = card;
        state.calledColor = "";

        listener.cardPlayed(i, name, card);
        log.info("Card played: player={}, card={}, remainingHand={}", name, card, hand.size());

        String rank = rules.rank(card);
        if (rank.equals("WILD") || rank.equals("WILD_DRAW_FOUR")) {
            String color = agent(i).chooseWildColor(state, hand);
            state.calledColor = color;
            listener.colorChosen(i, name, color);
            log.info("Color chosen: player={}, color={}", name, color);
        }

        if (hand.size() == 1) {
            if (agent(i).callUno(state, hand)) {
                listener.unoCalled(i, name);
                log.info("UNO called: player={}", name);
            } else {
                listener.missedUno(i, name);
                log.info("Missed UNO: player={} draws 2 penalty", name);
                engine.giveCardToCurrentPlayer(2);
            }
        }
    }

    private PlayerAgent agent(int index) {
        return agents.get(index);
    }

    /** Sum of all non-winner card values, awarded to the round winner. */
    int scoreOthers(int winner) {
        int points = 0;
        for (int i = 0; i < state.hands.size(); i++) {
            if (i == winner) {
                continue;
            }
            for (String c : state.hands.get(i)) {
                points += rules.cardValue(c);
            }
        }
        return points;
    }

    /** Builds, shuffles, and deals a new deck; sets a non-wild starting up card. */
    private void deal() {
        state.deck.clear();
        state.deck.addAll(DeckFactory.standardDeck());
        Collections.shuffle(state.deck, random);

        state.discard.clear();

        for (List<String> hand : state.hands) {
            hand.clear();
        }
        for (List<String> hand : state.hands) {
            for (int i = 0; i < 7; i++) {
                hand.add(engine.draw());
            }
        }

        state.upCard = engine.draw();
        while (state.upCard.startsWith("W")) {
            state.discard.add(state.upCard);
            state.upCard = engine.draw();
        }

        state.calledColor = "";
    }
}
