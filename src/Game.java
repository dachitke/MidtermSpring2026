import java.util.ArrayList;
import java.util.Random;

public class Game {

    private ArrayList<Player> players = new ArrayList<>();
    private Deck deck;
    private Card upCard;

    public Game() {
        deck = new Deck(new Random());
    }

    public void setup() {
        players.add(new HumanPlayer("You"));
        players.add(new BotPlayer("Bot1"));
        players.add(new BotPlayer("Bot2"));

        for (Player p : players) {
            for (int i = 0; i < 7; i++) {
                p.drawCard(deck);
            }
        }

        upCard = deck.draw();
    }

    public void start() {
        while (true) {
            // turn loop
        }
    }
}