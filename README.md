# Midterm UNO CLI

A standalone CLI UNO-like game, built as a standard Maven (Java 21) project with
automated tests, file-based logging, Docker support, and persistent game history
(Hibernate/JPA + H2).

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

Run the packaged jar:

```bash
java -jar target/midterm-uno-cli.jar --bots 3 --games 1
```

Or run directly from sources without packaging:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 1"
```

Interactive game (you + 2 bots):

```bash
java -jar target/midterm-uno-cli.jar --human --bots 2 --games 1
```

### Command-line options

| Flag | Meaning | Default |
|------|---------|---------|
| `--bots N` | number of bot players | `3` |
| `--games N` | number of games to play | `1` |
| `--human` | add a human player | off |
| `--seed N` | RNG seed for reproducible games | current time |
| `--stats` | print persisted game-history reports and exit | off |
| `--no-db` | play without persisting results | off |

Card input examples (interactive mode):

```text
R5   red 5        YS   yellow skip     BR   blue reverse
G+2  green draw two   W    wild        W4   wild draw four
draw draw a card
```

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
docker run --rm midterm-uno-cli --bots 3 --games 1 --seed 42
```

Interactive game (allocate a TTY for stdin):

```bash
docker run --rm -it midterm-uno-cli --human --bots 2 --games 1
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
java -jar target/midterm-uno-cli.jar --bots 3 --games 5   # play (auto-persists)
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
