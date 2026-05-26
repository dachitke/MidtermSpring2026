import java.util.ArrayList;

public class GameState {

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
}