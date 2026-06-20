# Final Project Report: UNO

## 1. UNO Rules Implemented

The project implements a fuller subset of UNO (see `docs/rules-supported.md` for
the full matrix):

- Standard **108-card deck** (`DeckFactory`).
- **Legal-play validation** by colour, number, or action type, plus Wilds.
- All action cards: **Skip**, **Reverse** (2-player = Skip), **Draw Two**,
  **Wild**, **Wild Draw Four**.
- **Draw/Pass**: draw one when no legal play; play it if legal, else pass.
- **UNO call** with a **missed-UNO 2-card penalty** (simplified timing).
- **Round scoring** (opponents' remaining card values) and a **multi-round
  match to a target score** (default 500) with a final winner.

## 2. How To Play From The CLI

Build and run (see `README.md` for full commands):

```bash
./mvnw clean package
java -jar target/midterm-uno-cli.jar --human --bots 2 --target 500
```

On your turn the console shows the up card and your hand as `index:card`
(e.g. `0:R5 1:G+2 2:W`). You type:

- a hand **index** (or the card text like `R5`) to play it,
- `DRAW` to draw a card (you'll be asked whether to play a drawn legal card),
- `R/Y/G/B` to choose a colour after a Wild,
- `y/n` when asked to call UNO at one card.

Bot-only games run automatically:

```bash
java -jar target/midterm-uno-cli.jar --bots 3 --target 500
```

Game history and statistics (persisted via Assignment 5):

```bash
java -jar target/midterm-uno-cli.jar --stats
```

## 3. Architecture: Game Logic vs CLI

Rules and flow are deliberately decoupled from the console:

- **Rules / state (no I/O):** `GameRules` (legality + card values),
  `GameState` (data), `GameEngine` (turn advance, draw, action-card effects),
  `DeckFactory` (deck composition), `BotAI` (bot choices).
- **Flow controllers (no I/O):** `RoundController` runs one round (turn loop,
  draw/pass, UNO penalty, scoring); `MatchController` runs rounds to the target
  score and decides the winner. Both are driven through two abstractions:
  - `PlayerAgent` — a player's decisions (`BotAgent`, `HumanAgent`, and the test
    `ScriptedAgent`),
  - `GameListener` — output events.
- **CLI layer (the only console code):** `ConsoleGameListener` renders events,
  `HumanAgent` reads input, and `Game` wires everything together and maps the
  result to a persistable `GameResult`. `Main` parses arguments.

Because the controllers depend only on `PlayerAgent` and `GameListener`, the
entire game can run headless in tests with scripted players and a silent
listener — the CLI is **not** the only place rules exist.

Persistence (Assignment 5) lives in the `persistence` package and is invoked
only from `Main`; game logic never touches SQL.

## 4. Tests Added

`./mvnw test` runs 32 tests, including:

- `DeckFactoryTest` — deck composition (108 cards, per-colour counts, wilds).
- `GameRulesTest` — legality by colour/number/action, wild legality, called-colour
  legality, and card scoring values.
- `CardEffectTest` — Skip, Reverse (3-player and 2-player), Draw Two, Wild Draw
  Four effects via `GameEngine`.
- `RoundControllerTest` — scoring, draw-then-pass, UNO called (no penalty),
  missed UNO (2-card penalty), wild colour selection.
- `MatchControllerTest` — multi-round play until the target score and winner
  selection.
- `GameEngineTest` — original skip characterization test (still passing).
- `GameHistoryRepositoryTest` — persistence layer on isolated in-memory H2.

## 5. Remaining Limitations

- No Wild Draw Four **challenge** rule and no **stacking** of Draw cards.
- Missed-UNO uses **simplified timing** (immediate penalty on reaching one card)
  rather than the "caught before next player acts" window.
- Starting **action** cards are not applied (only Wild is redrawn).
- `BotAI` is intentionally simple (priority-based) and its `cardPriority` has a
  legacy `"NUMBER"` branch that never matches; bots still play correctly but not
  optimally.
- Human play to the default 500-point target is long; use `--target` to shorten.
