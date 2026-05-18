public class GameRules {

    public boolean canPlay(String card, String upCard) {
        if (card == null || upCard == null) {
            return false;
        }

        if ("W".equals(card)) {
            return true;
        }

        return color(card).equals(color(upCard))
                || rank(card).equals(rank(upCard));
    }

    private String color(String card) {
        if (card.startsWith("R")) return "R";
        if (card.startsWith("Y")) return "Y";
        if (card.startsWith("G")) return "G";
        if (card.startsWith("B")) return "B";
        return "";
    }

    private String rank(String card) {
        if (card.equals("W")) return "WILD";
        if (card.equals("W4")) return "WILD_DRAW_FOUR";
        if (card.endsWith("S")) return "SKIP";
        if (card.endsWith("R")) return "REVERSE";
        if (card.endsWith("+2")) return "DRAW_TWO";
        return "NUMBER";
    }
}