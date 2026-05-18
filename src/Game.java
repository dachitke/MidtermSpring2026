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

    Random random = new Random();
    Scanner scanner = new Scanner(System.in);

     void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<String>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<String>());
        }
    }
    void buildDeck() {
        String[] colors = {"R", "Y", "G", "B"};

        for (int c = 0; c < colors.length; c++) {
            deck.add(colors[c] + "0");

            for (int n = 1; n <= 9; n++) {
                deck.add(colors[c] + n);
                deck.add(colors[c] + n);
            }

            deck.add(colors[c] + "S");
            deck.add(colors[c] + "S");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "R");
            deck.add(colors[c] + "+2");
            deck.add(colors[c] + "+2");
        }

        for (int i = 0; i < 4; i++) {
            deck.add("W");
            deck.add("W4");
        }
    }
    String draw() {
        if (deck.isEmpty()) {
            deck.addAll(discard);
            discard.clear();
            Collections.shuffle(deck, random);
        }
        if (deck.isEmpty()) {
            return "W";
        }
        return deck.remove(0);
    }
    void startRoundSetup() {
        deck.clear();
        buildDeck();
        Collections.shuffle(deck, random);

        discard.clear();

        for (int i = 0; i < hands.size(); i++) {
            hands.get(i).clear();
        }

        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(draw());
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

}