# Supported UNO Rules

This lists which rules from `Final_Project_UNO_rules_reference.md` are implemented
and the variants/simplifications used.

## Implemented

| Rule | Status | Notes |
|------|--------|-------|
| Deck composition | Full | Standard 108-card deck (`DeckFactory`): 4 colours, one 0 + two 1-9 per colour, two each of Skip/Reverse/Draw Two per colour, 4 Wild, 4 Wild Draw Four. Verified by `DeckFactoryTest`. |
| Legal play validation | Full | Match by colour, number, or action type; Wild/Wild Draw Four always playable; illegal plays rejected (`GameRules.canPlay`). |
| Skip | Full | Next player loses their turn; works in multi-player flow (`GameEngine`). |
| Reverse | Full | Reverses direction for 3+ players; **acts like Skip in a 2-player game** (documented variant). |
| Draw Two | Full | Next player draws two cards and loses their turn. No stacking. |
| Wild | Full | Player chooses the active colour; choice affects later legal-play checks. |
| Wild Draw Four | Full | Player chooses colour; next player draws four and loses their turn. No challenge rule. |
| Draw / Pass | Full | If no legal play, the player draws one card; if it is legal they may play it immediately, otherwise they pass. |
| UNO call + missed-UNO penalty | Implemented (simplified timing) | When a play leaves a player with one card they may call UNO; failing to call results in an immediate 2-card penalty. |
| Round scoring | Full | Round winner scores the sum of opponents' remaining card values (numbers face value; Skip/Reverse/Draw Two = 20; Wild/Wild Draw Four = 50). |
| Multi-round to target | Full | Rounds repeat until a player reaches the target score (default 500, configurable via `--target`); highest total wins. |

## Variants & Simplifications

These affect visible gameplay and are intentional:

1. **2-player Reverse = Skip.** With two players, Reverse skips the opponent so
   the player who reversed plays again.
2. **Missed-UNO timing.** Official UNO penalises a missed UNO only if caught
   before the next player acts. Here the check is simplified: the moment a play
   leaves you with one card, you either call UNO or immediately draw 2. Bots
   always call; humans are prompted.
3. **Draw/Pass variant.** "Draw one, play it if legal, else pass" (the first
   variant in the reference). A drawn legal card may be played the same turn.
4. **No Wild Draw Four challenge.** Wild Draw Four is always accepted; the
   "only if no matching colour" challenge rule is not implemented.
5. **No Draw Two / Draw Four stacking.**
6. **Starting card.** The initial face-up card is redrawn only if it is a Wild;
   a starting action card (Skip/Reverse/Draw Two) is left as the top card and
   its effect is not applied.
7. **Bots auto-play a drawn legal card** and always call UNO (deterministic).
8. **Deterministic deck for tests.** Tests seed the RNG / construct fixed
   states; they do not rely on a real shuffle.

## Not Implemented

- Wild Draw Four challenge mechanic.
- Card stacking variants.
- "Jump-in" / "seven-zero" / other house variants.
