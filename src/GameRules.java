public class GameRules {

    public boolean canPlay(Card card, Card upCard) {
        if (card == null || upCard == null) {
            return false;
        }

        if ("W".equals(card.getColor())) {
            return true;
        }

        return card.getColor().equals(upCard.getColor())
                || card.getValue().equals(upCard.getValue());
    }
}
