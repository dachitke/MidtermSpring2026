package persistence.reports;

import java.time.LocalDateTime;

/** Row for the "highest scores" report. */
public record HighScore(String player, int points, LocalDateTime achievedAt) {
}
