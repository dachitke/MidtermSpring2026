/** Lightweight record of one finished round within a match. */
public final class RoundRecord {

    private final int roundNumber;
    private final int winner;
    private final int points;

    public RoundRecord(int roundNumber, int winner, int points) {
        this.roundNumber = roundNumber;
        this.winner = winner;
        this.points = points;
    }

    public int roundNumber() {
        return roundNumber;
    }

    public int winner() {
        return winner;
    }

    public int points() {
        return points;
    }
}
