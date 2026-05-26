import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {

    boolean quiet = false;

    Random random;
    Scanner scanner = new Scanner(System.in);
    GameRules gameRules = new GameRules();
    GameState state = new GameState();
    BotAI botAI = new BotAI(gameRules);

    GameEngine engine;

    public Game(int bots, boolean human, long seed) {
        this.random = new Random(seed);
        setupPlayers(bots, human);

        this.engine = new GameEngine(state, gameRules, random);
    }

    // main game
    public void playGame() {

        startRoundSetup();

        int guard = 0;

        while (guard++ < 3000) {

            String name = state.playerNames.get(state.currentPlayer);
            ArrayList<String> hand = state.hands.get(state.currentPlayer);

            showTurnInfo(name, hand);

            int chosen = chooseMove(hand);

            if (chosen == -1) {
                chosen = handleDraw(hand, name);
            }

            int playerBeforeTurn = state.currentPlayer;

            if (chosen >= 0) {
                playCard(chosen, hand, name);
            }

            if (isGameOver(playerBeforeTurn, name)) return;
        }

        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    // flow
    void playCard(int chosen, ArrayList<String> hand, String name) {

        if (chosen >= hand.size()) {
            if (!quiet) {
                System.out.println(name + " selected invalid index, draws penalty.");
            }
            hand.add(draw());

            return;
        }

        String card = hand.get(chosen);

        if (!gameRules.canPlay(card, state.upCard,state.calledColor)) {
            if (!quiet) {
                System.out.println(name + " played illegal card " + card);
            }
            hand.add(draw());
            return;
        }

        hand.remove(chosen);
        state.discard.add(state.upCard);
        state.upCard = card;
        state.calledColor = "";

        if (!quiet) {
            System.out.println(name + " plays " + card);
        }

        handleWild(card, name, hand);

        if (hand.size() == 1 && !quiet) {
            System.out.println(name + " says UNO!");
        }

        applyCardEffect(card);
    }

    boolean isGameOver(int playerIndex, String name){
        if (!state.hands.get(playerIndex).isEmpty()) return false;

        int points = 0;

        for (int i = 0; i < state.hands.size(); i++) {
            if (i == playerIndex) continue;

            for (String c : state.hands.get(i)) {
                points += points(c);
            }
        }

        state.scores[playerIndex] += points;

        if (!quiet) {
            System.out.println(name + " wins and scores " + points);
        }

        return true;
    }

    void handleWild(String card, String name, ArrayList<String> hand) {
        if (card.equals("W") || card.equals("W4")) {
            if (state.humanPlayers.get(state.currentPlayer)) {
                state.calledColor = askColor();
            } else {
                state.calledColor = botAI.chooseBotColor(hand);
            }

            if (!quiet) {
                System.out.println(name + " calls " + state.calledColor);
            }
        }
    }


    void setupPlayers(int bots, boolean human) {
        int players = bots + (human ? 1 : 0);
        if (players <= 1|| players>4) return;

        state.playerNames.clear();
        state.humanPlayers.clear();
        state.hands.clear();

        if (human) {
            state.playerNames.add("You");
            state.humanPlayers.add(true);
            state.hands.add(new ArrayList<>());
        }

        for (int i = 1; i <= bots; i++) {
            state.playerNames.add("Bot" + i);
            state.humanPlayers.add(false);
            state.hands.add(new ArrayList<>());
        }
    }

    void startRoundSetup() {

        state.deck.clear();
        buildDeck();
        Collections.shuffle(state.deck, random);

        state.discard.clear();

        for (ArrayList<String> hand : state.hands) {
            hand.clear();
        }

        for (ArrayList<String> hand : state.hands) {
            for (int i = 0; i < 7; i++) {
                hand.add(draw());
            }
        }

        state.upCard = draw();
        while (state.upCard.startsWith("W")) {
            state.discard.add(state.upCard);
            state.upCard = draw();
        }

        state.calledColor = "";
        state.direction = 1;
        state.currentPlayer = random.nextInt(state.playerNames.size());
    }

    void buildDeck() {
        String[] colors = {"R", "Y", "G", "B"};

        for (String c : colors) {
            state.deck.add(c + "0");

            for (int n = 1; n <= 9; n++) {
                state.deck.add(c + n);
                state.deck.add(c + n);
            }

            state.deck.add(c + "S");
            state.deck.add(c + "S");
            state.deck.add(c + "R");
            state.deck.add(c + "R");
            state.deck.add(c + "+2");
            state.deck.add(c + "+2");
        }

        for (int i = 0; i < 4; i++) {
            state.deck.add("W");
            state.deck.add("W4");
        }
    }

    //selection
    int chooseMove(ArrayList<String> hand) {
        if (isHuman()) return askHuman(hand);
        return botAI.chooseBotCard(state, hand);
    }

    boolean isHuman() {
        return state.humanPlayers.get(state.currentPlayer);
    }

    int handleDraw(ArrayList<String> hand, String name) {
        String drawn = drawCardToHand(hand, name);

        if (!gameRules.canPlay(drawn, state.upCard,state.calledColor)) {
            return -1;
        }

        if (!isHuman()) {
            return hand.size() - 1;
        }

        System.out.print("Play drawn card " + drawn + "? y/n: ");
        String answer = scanner.nextLine();

        return (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"))
                ? hand.size() - 1
                : -1;
    }
    int askHuman(ArrayList<String> hand) {

        while (true) {

            System.out.print("Choose index or DRAW: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("DRAW")) return -1;

            try {
                int idx = Integer.parseInt(input);
                if (idx >= 0 && idx < hand.size()) return idx;
            } catch (Exception ignored) {}

            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input) && gameRules.canPlay(hand.get(i), state.upCard,state.calledColor)) {
                    return i;
                }
            }

            System.out.println("Invalid move.");
        }
    }

    // draw
    String draw() {
        return engine.draw();
    }

    String drawCardToHand(ArrayList<String> hand, String name) {

        String drawn = draw();
        hand.add(drawn);

        if (!quiet) {
            System.out.println(name + " draws " + drawn);
        }

        return drawn;
    }

    // effects
    void applyCardEffect(String card) {
        engine.applyCardEffect(card);
    }


    void showTurnInfo(String name, ArrayList<String> hand) {

        if (quiet) return;

        System.out.println("\nUp card: " + state.upCard +
                (state.calledColor.isEmpty() ? "" : " called " + state.calledColor));

        System.out.println(name + " hand: " + join(hand));
    }

    // ui
    String askColor() {

        while (true) {

            System.out.print("Color R/Y/G/B: ");
            String c = scanner.nextLine().trim().toUpperCase();

            if (c.matches("[RYGB]")) return c;

            System.out.println("Invalid color.");
        }
    }
    int points(String card) {

        String r = gameRules.rank(card);


        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) return 20;
        if (r.contains("WILD")) return 50;
        else {return Integer.parseInt(r);}

    }

    // util
    String join(ArrayList<String> cards) {

        String out = "";

        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) out += " ";
        }

        return out;
    }
}