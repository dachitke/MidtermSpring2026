import java.util.ArrayList;

public class BotAI {

    GameRules rules;

    public BotAI(GameRules rules) {
        this.rules = rules;
    }

    int chooseBotCard(GameState state, ArrayList<String> hand) {

        int bestIndex = -1;
        int bestPriority = -1;

        for (int i = 0; i < hand.size(); i++) {

            String card = hand.get(i);

            if (!rules.canPlay(card, state.upCard, state.calledColor)) continue;

            int priority = cardPriority(card);

            if (priority > bestPriority) {
                bestPriority = priority;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    int cardPriority(String card) {

        String r = rules.rank(card);

        if (r.equals("DRAW_TWO")) return 4;
        if (r.equals("SKIP")) return 3;
        if (r.equals("NUMBER")) return 2;
        if (card.startsWith("W")) return 1;

        return 0;
    }

    String chooseBotColor(ArrayList<String> hand) {

        int r = 0, y = 0, g = 0, b = 0;

        for (String c : hand) {
            switch (c.charAt(0)) {
                case 'R' -> r++;
                case 'Y' -> y++;
                case 'G' -> g++;
                case 'B' -> b++;
            }
        }

        if (r >= y && r >= g && r >= b) return "R";
        if (y >= r && y >= g && y >= b) return "Y";
        if (g >= r && g >= y && g >= b) return "G";
        return "B";
    }
}