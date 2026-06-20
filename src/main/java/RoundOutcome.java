/** Result of a single round: who won (or -1) and the points they scored. */
public final class RoundOutcome {

    private final int winner;
    private final int points;

    public RoundOutcome(int winner, int points) {
        this.winner = winner;
        this.points = points;
    }

    /** Index of the round winner, or {@code -1} if the round ended without one. */
    public int winner() {
        return winner;
    }

    public int points() {
        return points;
    }

    public boolean hasWinner() {
        return winner >= 0;
    }
}
