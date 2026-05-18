import java.util.ArrayList;
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
}