package persistence.reports;

import java.time.LocalDateTime;

/** Summary row for the "recent games" report. */
public record RecentGame(Long gameId, LocalDateTime startedAt, LocalDateTime endedAt,
                         String winner, int roundsPlayed) {
}
