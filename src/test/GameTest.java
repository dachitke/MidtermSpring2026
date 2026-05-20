import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void rankShouldDetectDrawTwo() {
        Game game = new Game(3, false, 1);

        assertEquals("DRAW_TWO", game.rank("R+2"));
    }

    @Test
    void rankShouldDetectWildDrawFour() {
        Game game = new Game(3, false, 1);

        assertEquals("WILD_DRAW_FOUR", game.rank("W4"));
    }

    @Test
    void numberShouldReturnCardNumber() {
        Game game = new Game(3, false, 1);

        assertEquals(7, game.number("R7"));
    }

    @Test
    void pointsShouldReturnWildPoints() {
        Game game = new Game(3, false, 1);

        assertEquals(50, game.points("W4"));
    }

    @Test
    void pointsShouldReturnNumberPoints() {
        Game game = new Game(3, false, 1);

        assertEquals(5, game.points("R5"));
    }

    @Test
    void chooseBotColorShouldChooseMostCommonColor() {

        Game game = new Game(3, false, 1);

        ArrayList<String> hand = new ArrayList<>();

        hand.add("B1");
        hand.add("B2");
        hand.add("R3");

        assertEquals("B", game.chooseBotColor(hand));
    }

    @Test
    void chooseBotCardShouldPreferBetterMove() {

        Game game = new Game(3, false, 1);

        ArrayList<String> hand = new ArrayList<>();

        hand.add("B3");
        hand.add("R4");
        hand.add("W");

        game.upCard = "R9";

        assertEquals(1, game.chooseBotCard(hand));
    }

    @Test
    void colorShouldReturnCorrectColor() {

        Game game = new Game(3, false, 1);

        assertEquals("R", game.color("R5"));
    }

    @Test
    void nextShouldMovePlayerForward() {

        Game game = new Game(3, false, 1);

        game.currentPlayer = 0;

        game.next();

        assertEquals(1, game.currentPlayer);
    }

    @Test
    void reverseDirectionShouldMoveBackward() {

        Game game = new Game(3, false, 1);

        game.currentPlayer = 1;
        game.direction = -1;

        game.next();

        assertEquals(0, game.currentPlayer);
    }
}