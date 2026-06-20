package persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** One persisted game: when it ran, how many rounds, and who won. */
@Entity
@Table(name = "game")
public class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @ManyToOne
    @JoinColumn(name = "winner_player_id")
    private Player winner;

    @Column(name = "rounds_played", nullable = false)
    private int roundsPlayed;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Round> rounds = new ArrayList<>();

    protected GameRecord() {
        // required by JPA
    }

    public GameRecord(LocalDateTime startedAt, LocalDateTime endedAt, Player winner, int roundsPlayed) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.winner = winner;
        this.roundsPlayed = roundsPlayed;
    }

    public void addRound(Round round) {
        round.setGame(this);
        rounds.add(round);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public Player getWinner() {
        return winner;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public List<Round> getRounds() {
        return rounds;
    }
}
