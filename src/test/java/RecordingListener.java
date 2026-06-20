import java.util.ArrayList;
import java.util.List;

/** Captures game events as strings so tests can assert what happened. */
class RecordingListener implements GameListener {

    final List<String> events = new ArrayList<>();

    @Override
    public void unoCalled(int player, String name) {
        events.add("UNO:" + player);
    }

    @Override
    public void missedUno(int player, String name) {
        events.add("MISSED_UNO:" + player);
    }

    @Override
    public void colorChosen(int player, String name, String color) {
        events.add("COLOR:" + player + ":" + color);
    }

    @Override
    public void passed(int player, String name) {
        events.add("PASS:" + player);
    }

    @Override
    public void cardDrawn(int player, String name, String card) {
        events.add("DRAW:" + player + ":" + card);
    }

    @Override
    public void roundEnded(int winner, String name, int points, int[] totals, List<String> playerNames) {
        events.add("ROUND_WIN:" + winner + ":" + points);
    }

    boolean has(String event) {
        return events.contains(event);
    }
}
