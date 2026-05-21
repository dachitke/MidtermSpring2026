import java.util.*;

public class Deck {
    private ArrayList<Card> cards = new ArrayList<>();
    private Random random;

    public Deck(Random random) {
        this.random = random;
        buildDeck();
        shuffle();
    }

    public void buildDeck() {
        String[] colors = {"R", "Y", "G", "B"};

        for (String color : colors) {
            cards.add(new Card(color, "0"));

            for (int i = 1; i <= 9; i++) {
                cards.add(new Card(color, String.valueOf(i)));
                cards.add(new Card(color, String.valueOf(i)));
            }

            cards.add(new Card(color, "S"));
            cards.add(new Card(color, "S"));
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new Card("W", ""));
        }
        cards.add(new Card("W", "4"));
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card draw() {
        return cards.remove(0);
    }
}