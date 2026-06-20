package persistence.reports;

/** Row for the "player win count" report. */
public record WinCount(String player, long wins) {
}
