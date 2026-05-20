import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameRulesTest {

    GameRules rules = new GameRules();

    @Test
    void sameColorIsLegal() {
        assertTrue(rules.canPlay("R5", "R9"));
    }

    @Test
    void sameNumberIsLegal() {
        assertTrue(rules.canPlay("G5", "R5"));
    }

    @Test
    void wildIsAlwaysLegal() {
        assertTrue(rules.canPlay("W", "R9"));
    }

    @Test
    void wildDrawFourIsAlwaysLegal() {
        assertTrue(rules.canPlay("W4", "B2"));
    }

    @Test
    void differentColorAndNumberIsIllegal() {
        assertFalse(rules.canPlay("B3", "R9"));
    }

    @Test
    void nullCardIsIllegal() {
        assertFalse(rules.canPlay(null, "R9"));
    }

    @Test
    void nullUpCardIsIllegal() {
        assertFalse(rules.canPlay("R5", null));
    }

    @Test
    void sameValueDifferentColorIsLegalOnlyIfNumber() {
        assertTrue(rules.canPlay("G9", "R9"));
    }
}