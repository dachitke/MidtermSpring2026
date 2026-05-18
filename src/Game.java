import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {


    ArrayList<String> playerNames = new ArrayList<>();
    ArrayList<Boolean> humanPlayers = new ArrayList<>();
    ArrayList<ArrayList<String>> hands = new ArrayList<>();

    ArrayList<String> deck = new ArrayList<>();
    ArrayList<String> discard = new ArrayList<>();

    int[] scores = new int[10];

    int currentPlayer = 0;
    int direction = 1;

    String upCard = "";
    String calledColor = "";

    boolean quiet = false;

    Random random;
    Scanner scanner = new Scanner(System.in);

    GameRules gameRules = new GameRules();


    public Game(long seed) {
        this.random = new Random(seed);
    }

    // main game
    public void playGame(int bots, boolean human) {
        setupPlayers(bots, human);
        startRoundSetup();

        int guard = 0;

        while (guard++ < 3000) {

            String name = playerNames.get(currentPlayer);
            ArrayList<String> hand = hands.get(currentPlayer);

            showTurnInfo(name, hand);

            int chosen = chooseMove(hand);

            if (chosen == -1) {
                chosen = handleDraw(hand, name);
            }

            if (chosen >= 0) {
                playCard(chosen, hand, name);
            } else {
                next();
            }

            if (isGameOver(name)) return;
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
            next();
            return;
        }

        String card = hand.get(chosen);

        if (!gameRules.canPlay(card, upCard)) {
            if (!quiet) {
                System.out.println(name + " played illegal card " + card);
            }
            hand.add(draw());
            next();
            return;
        }

        hand.remove(chosen);
        discard.add(upCard);
        upCard = card;
        calledColor = "";

        if (!quiet) {
            System.out.println(name + " plays " + card);
        }

        handleWild(card, name, hand);

        if (hand.size() == 1 && !quiet) {
            System.out.println(name + " says UNO!");
        }

        applyCardEffect(card);
    }

    boolean isGameOver(String name) {
        if (!hands.get(currentPlayer).isEmpty()) return false;

        int points = 0;

        for (int i = 0; i < hands.size(); i++) {
            if (i == currentPlayer) continue;

            for (String c : hands.get(i)) {
                points += points(c);
            }
        }

        scores[currentPlayer] += points;

        if (!quiet) {
            System.out.println(name + " wins and scores " + points);
        }

        return true;
    }

    void handleWild(String card, String name, ArrayList<String> hand) {
        if (card.equals("W") || card.equals("W4")) {
            if (humanPlayers.get(currentPlayer)) {
                calledColor = askColor();
            } else {
                calledColor = chooseBotColor(hand);
            }

            if (!quiet) {
                System.out.println(name + " calls " + calledColor);
            }
        }
    }


    void setupPlayers(int bots, boolean human) {

        playerNames.clear();
        humanPlayers.clear();
        hands.clear();

        if (human) {
            playerNames.add("You");
            humanPlayers.add(true);
            hands.add(new ArrayList<>());
        }

        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(false);
            hands.add(new ArrayList<>());
        }
    }

    void startRoundSetup() {

        deck.clear();
        buildDeck();
        Collections.shuffle(deck, random);

        discard.clear();

        for (ArrayList<String> hand : hands) {
            hand.clear();
        }

        for (ArrayList<String> hand : hands) {
            for (int i = 0; i < 7; i++) {
                hand.add(draw());
            }
        }

        upCard = draw();
        while (upCard.startsWith("W")) {
            discard.add(upCard);
            upCard = draw();
        }

        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());
    }

    void buildDeck() {
        String[] colors = {"R", "Y", "G", "B"};

        for (String c : colors) {
            deck.add(c + "0");

            for (int n = 1; n <= 9; n++) {
                deck.add(c + n);
                deck.add(c + n);
            }

            deck.add(c + "S");
            deck.add(c + "S");
            deck.add(c + "R");
            deck.add(c + "R");
            deck.add(c + "+2");
            deck.add(c + "+2");
        }

        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
    }

    //selection
    int chooseMove(ArrayList<String> hand) {
        if (isHuman()) return askHuman(hand);
        return chooseBotCard(hand);
    }

    boolean isHuman() {
        return humanPlayers.get(currentPlayer);
    }

    int handleDraw(ArrayList<String> hand, String name) {
        String drawn = drawCardToHand(hand, name);

        if (!gameRules.canPlay(drawn, upCard)) {
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

    int chooseBotCard(ArrayList<String> hand) {

        int bestIndex = -1;
        int bestPriority = -1;

        for (int i = 0; i < hand.size(); i++) {

            String card = hand.get(i);

            if (!gameRules.canPlay(card, upCard)) continue;

            int priority = cardPriority(card);

            if (priority > bestPriority) {
                bestPriority = priority;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    int cardPriority(String card) {

        String r = rank(card);

        if (r.equals("DRAW_TWO")) return 4;
        if (r.equals("SKIP")) return 3;
        if (r.equals("NUMBER")) return 2;
        if (card.startsWith("W")) return 1;

        return 0;
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
                if (hand.get(i).equals(input) && gameRules.canPlay(hand.get(i), upCard)) {
                    return i;
                }
            }

            System.out.println("Invalid move.");
        }
    }

    // draw
    String draw() {

        if (deck.isEmpty()) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }

        if (deck.isEmpty()) return "W";

        return deck.remove(0);
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

        String rank = rank(card);

        if (rank.equals("SKIP")) {
            next();
            next();

        } else if (rank.equals("REVERSE")) {
            direction *= -1;

            next();

        } else if (rank.equals("DRAW_TWO")) {

            next();

            hands.get(currentPlayer).add(draw());
            hands.get(currentPlayer).add(draw());

            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }

            next();

        } else if (rank.equals("WILD_DRAW_FOUR")) {

            next();

            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(draw());
            }

            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws four.");
            }

            next();

        } else {
            next();
        }
    }

    // turn control
    void next() {
        currentPlayer += direction;

        if (currentPlayer >= playerNames.size()) currentPlayer = 0;
        if (currentPlayer < 0) currentPlayer = playerNames.size() - 1;
    }

    void showTurnInfo(String name, ArrayList<String> hand) {

        if (quiet) return;

        System.out.println("\nUp card: " + upCard +
                (calledColor.isEmpty() ? "" : " called " + calledColor));

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

    String chooseBotColor(ArrayList<String> hand) {

        int r = 0, y = 0, g = 0, b = 0;

        for (String c : hand) {
            switch (color(c)) {
                case "R" -> r++;
                case "Y" -> y++;
                case "G" -> g++;
                case "B" -> b++;
            }
        }

        if (r >= y && r >= g && r >= b) return "R";
        if (y >= r && y >= g && y >= b) return "Y";
        if (g >= r && g >= y && g >= b) return "G";
        return "B";
    }

    // rules
    boolean isLegal(String card, String up, String call) {

        if (card.startsWith("W")) return true;

        if (color(card).equals(color(up))) return true;

        if (!call.isEmpty() && color(card).equals(call)) return true;

        if (rank(card).equals(rank(up)) && !rank(card).equals("NUMBER")) return true;

        return rank(card).equals("NUMBER")
                && rank(up).equals("NUMBER")
                && number(card) == number(up);
    }

    String color(String card) {
        if (card.startsWith("R")) return "R";
        if (card.startsWith("Y")) return "Y";
        if (card.startsWith("G")) return "G";
        if (card.startsWith("B")) return "B";
        return "";
    }

    String rank(String card) {
        if (card.equals("W")) return "WILD";
        if (card.equals("W4")) return "WILD_DRAW_FOUR";
        if (card.endsWith("S")) return "SKIP";
        if (card.endsWith("R")) return "REVERSE";
        if (card.endsWith("+2")) return "DRAW_TWO";
        return "NUMBER";
    }

    int number(String card) {
        if (!rank(card).equals("NUMBER")) return -1;
        return Integer.parseInt(card.substring(1));
    }

    int points(String card) {

        String r = rank(card);

        if (r.equals("NUMBER")) return number(card);
        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) return 20;
        if (r.contains("WILD")) return 50;

        return 0;
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