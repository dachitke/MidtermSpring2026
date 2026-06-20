# UNO CLI

A standalone command-line UNO game built as a standard Maven (Java 21) project.
It supports a fuller set of UNO rules (action cards, Wilds, UNO calls, round
scoring, and multi-round play to a target score) with the game logic separated
from the console so rules are testable without a terminal. It also includes
file-based logging, Docker support, and persistent game history (Hibernate/JPA + H2).

Supported rules are documented in [`docs/rules-supported.md`](docs/rules-supported.md)
and the design is described in [`docs/final-report.md`](docs/final-report.md).

## Requirements

* JDK 21 (only needed for local builds; the Docker build is self-contained)
* Maven — or just use the bundled wrapper (`./mvnw` / `mvnw.cmd`), no install required
* Docker (optional, for the containerized run)

## Project Layout

```text
src/main/java        application sources
src/main/resources   logback.xml (logging configuration)
src/test/java        JUnit 5 characterization tests
pom.xml              Maven build configuration
Dockerfile           multi-stage build + run image
```

## Local Build

Compile the project:

```bash
./mvnw clean compile        # Windows: mvnw.cmd clean compile
```

## Local Test

Run the JUnit 5 tests through Maven (no manual classpath setup needed):

```bash
./mvnw test                 # Windows: mvnw.cmd test
```

## Package

Build a self-contained executable jar at `target/midterm-uno-cli.jar`
(includes the logging backend). Tests run as part of packaging:

```bash
./mvnw clean package        # Windows: mvnw.cmd clean package
```

## Local Run

A "game" is a **match**: rounds are played until a player reaches the target
score (default 500), and the highest total wins.

Run the packaged jar (bot-only match to a short target):

```bash
java -jar target/midterm-uno-cli.jar --bots 3 --target 200
```

Or run directly from sources without packaging:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --target 200"
```

Interactive game (you + 2 bots, full 500-point match):

```bash
java -jar target/midterm-uno-cli.jar --human --bots 2 --target 500
```

### Command-line options

| Flag | Meaning | Default |
|------|---------|---------|
| `--bots N` | number of bot players | `3` |
| `--target N` | points needed to win the match | `500` |
| `--games N` | number of matches to play | `1` |
| `--human` | add a human player | off |
| `--seed N` | RNG seed for reproducible games | current time |
| `--stats` | print persisted game-history reports and exit | off |
| `--no-db` | play without persisting results | off |

Total players (bots + human) must be between 2 and 4.

On your turn the console shows the up card and your hand as `index:card`. Enter:

```text
0           play the card at hand index 0
R5          play a specific card (red 5)
DRAW        draw a card
R/Y/G/B     choose a color after a Wild
y / n       call UNO when down to one card
```

Card encoding: `R5` red 5, `YS` yellow skip, `BR` blue reverse, `G+2` green draw
two, `W` wild, `W4` wild draw four.

## Docker Build

```bash
docker build -t midterm-uno-cli .
```

The build runs the tests and packages the jar inside the image, so it does not
depend on any locally installed JDK or Maven.

## Docker Run

Default bot-only game:

```bash
docker run --rm midterm-uno-cli
```

Pass game options as arguments:

```bash
docker run --rm midterm-uno-cli --bots 3 --target 200 --seed 42
```

Interactive game (allocate a TTY for stdin):

```bash
docker run --rm -it midterm-uno-cli --human --bots 2 --target 500
```

## Logging

Important game events (game start, each turn, cards played/drawn, invalid input,
and round/game end) are logged via SLF4J + Logback. To keep the CLI readable for
players, logs are written to a **file** rather than the console:

* Local runs: `logs/uno.log`
* Docker runs: `/app/logs/uno.log` inside the container

Change the log directory with `-Duno.log.dir=/some/path`. To persist Docker logs
on the host, mount a volume:

```bash
docker run --rm -v "$(pwd)/logs:/app/logs" midterm-uno-cli
```

## Game History & Statistics

Game results are persisted with **Hibernate/JPA** to an embedded **H2** database
(`./data/uno.mv.db`). After every game the app stores player names, start/end
timestamps, rounds played, per-player scores, and the winner.

View the reports:

```bash
java -jar target/midterm-uno-cli.jar --stats
```

This prints **recent games**, **player win counts**, and **highest scores**.
Typical flow:

```bash
java -jar target/midterm-uno-cli.jar --bots 3 --target 200   # play (auto-persists)
java -jar target/midterm-uno-cli.jar --stats              # view history
```

Database/ORM details, schema, configuration (including how to point at another
database via environment variables), and how to run the persistence tests are
documented in [`docs/database.md`](docs/database.md).

## Rules

See `docs/rules.html` for the implemented game rules.

## Submission

Submit your work through GitHub:

1. Fork this repository to your GitHub account.
2. Clone your fork locally.
3. Complete the work in your fork.
4. Commit your changes with clear commit messages.
5. Push your branch to GitHub.
6. Open a pull request from your fork back to the original repository.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path
