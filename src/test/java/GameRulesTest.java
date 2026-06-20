import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameRulesTest {

    GameRules rules = new GameRules();

    @Test
    void sameColorIsLegal() {
        assertTrue(rules.canPlay("R5", "R9",""));
    }

    @Test
    void sameNumberIsLegal() {
        assertTrue(rules.canPlay("G5", "R5",""));
    }

    @Test
    void wildIsAlwaysLegal() {
        assertTrue(rules.canPlay("W", "R9",""));
    }

    @Test
    void wildDrawFourIsAlwaysLegal() {
        assertTrue(rules.canPlay("W4", "B2",""));
    }

    @Test
    void differentColorAndNumberIsIllegal() {
        assertFalse(rules.canPlay("B3", "R9",""));
    }

    @Test
    void nullCardIsIllegal() {
        assertFalse(rules.canPlay(null, "R9",""));
    }

    @Test
    void nullUpCardIsIllegal() {
        assertFalse(rules.canPlay("R5", null,""));
    }

    @Test
    void sameValueDifferentColorIsLegalOnlyIfNumber() {
        assertTrue(rules.canPlay("G9", "R9",""));
    }

    @Test
    void matchByActionTypeIsLegal() {
        assertTrue(rules.canPlay("GS", "RS", ""));   // skip on skip
        assertTrue(rules.canPlay("B+2", "Y+2", "")); // draw two on draw two
    }

    @Test
    void calledColorAfterWildControlsLegality() {
        // Top card is a wild; the called colour is green.
        assertTrue(rules.canPlay("G5", "W", "G"));
        assertFalse(rules.canPlay("B5", "W", "G"));
    }

    @Test
    void cardValuesFollowStandardScoring() {
        assertEquals(5, rules.cardValue("R5"));
        assertEquals(0, rules.cardValue("G0"));
        assertEquals(20, rules.cardValue("YS"));
        assertEquals(20, rules.cardValue("BR"));
        assertEquals(20, rules.cardValue("G+2"));
        assertEquals(50, rules.cardValue("W"));
        assertEquals(50, rules.cardValue("W4"));
    }
}