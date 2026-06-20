# Database & Persistence (Assignment 5)

This project persists UNO game history and exposes statistics reports.

## Selected Database

**H2** — an embedded Java SQL database.

* **Runtime:** file-based at `./data/uno.mv.db` (history survives between runs).
* **Tests:** in-memory (`jdbc:h2:mem:...`, `create-drop`) — each test gets a fresh,
  isolated database, so tests never depend on local machine state.

H2 needs no separate server install, which makes it suitable for local
development and CI.

## Selected ORM / Persistence Framework

**Hibernate ORM 6 (Jakarta Persistence / JPA)**.

* Entities are mapped with JPA annotations.
* All database access goes through a DAO (`persistence.GameHistoryRepository`)
  using JPQL. Game logic contains **no raw SQL** — the game produces a plain
  `GameResult` object that the repository persists.

## Schema

Hibernate generates/updates the schema automatically (`hibernate.hbm2ddl.auto=update`),
so no manual setup step is required. The resulting tables:

```text
player(id PK, name UNIQUE NOT NULL)

game(id PK,
     started_at NOT NULL,
     ended_at,
     winner_player_id  FK -> player(id),
     rounds_played NOT NULL)

round(id PK,
      game_id FK -> game(id) NOT NULL,
      round_number NOT NULL,
      winner_player_id FK -> player(id))

score(id PK,
      round_id FK -> round(id) NOT NULL,
      player_id FK -> player(id) NOT NULL,
      points NOT NULL)
```

Relationships:

* a **game** has many **rounds** (one per played round; the current engine plays
  one round per game),
* a **round** has many **scores** (one per player),
* **game.winner** / **round.winner** reference **player**,
* timestamps live on **game** (`started_at`, `ended_at`).

The equivalent DDL is also written as a reference script at
[`src/main/resources/schema.sql`](../src/main/resources/schema.sql) (documentation
only; the application relies on Hibernate auto-DDL).

## Configuration & Credentials

Defaults live in [`src/main/resources/META-INF/persistence.xml`](../src/main/resources/META-INF/persistence.xml)
(persistence unit `uno`). H2's embedded `sa` user with an empty password is the
conventional embedded setup and contains no secret.

To point at a different database **without editing source**, set environment
variables (or `-D` system properties). `persistence.PersistenceManager` reads:

| Variable | Meaning |
|----------|---------|
| `UNO_DB_URL` | JDBC URL |
| `UNO_DB_USER` | database user |
| `UNO_DB_PASSWORD` | database password |

```bash
# example: use a local PostgreSQL instead of H2
set UNO_DB_URL=jdbc:postgresql://localhost:5432/uno   # PowerShell: $env:UNO_DB_URL=...
set UNO_DB_USER=uno
set UNO_DB_PASSWORD=...   # supplied from your environment, never committed
```

## Persisted Data

After each game completes, the app stores: player names, game start/end
timestamps, rounds played, per-player scores, and the final winner.

## Reports / Viewing History

Three reports are available via the `--stats` CLI mode:

```bash
java -jar target/midterm-uno-cli.jar --stats
```

* **Recent games** — most recently played games with winner and round count.
* **Player win counts** — games won per player, highest first.
* **Highest scores** — top single-round scores across all games.

Play games first (they persist automatically), then view stats:

```bash
java -jar target/midterm-uno-cli.jar --bots 3 --games 5
java -jar target/midterm-uno-cli.jar --stats
```

Use `--no-db` to play without persisting.

## Running Persistence Tests

```bash
./mvnw test                 # Windows: mvnw.cmd test
```

Persistence tests live in `src/test/java/persistence/GameHistoryRepositoryTest.java`
and run against a throwaway in-memory H2 database created per test.

## Inspecting the Database Directly (optional)

The H2 jar includes a web console:

```bash
java -cp target/midterm-uno-cli.jar org.h2.tools.Console
```

Connect with JDBC URL `jdbc:h2:file:./data/uno`, user `sa`, empty password.
