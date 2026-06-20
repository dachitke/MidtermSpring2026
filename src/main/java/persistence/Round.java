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

import java.util.ArrayList;
import java.util.List;

/** One round within a game, with its winner and per-player scores. */
@Entity
@Table(name = "round")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_id")
    private GameRecord game;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @ManyToOne
    @JoinColumn(name = "winner_player_id")
    private Player winner;

    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Score> scores = new ArrayList<>();

    protected Round() {
        // required by JPA
    }

    public Round(int roundNumber, Player winner) {
        this.roundNumber = roundNumber;
        this.winner = winner;
    }

    public void addScore(Score score) {
        score.setRound(this);
        scores.add(score);
    }

    public Long getId() {
        return id;
    }

    void setGame(GameRecord game) {
        this.game = game;
    }

    public GameRecord getGame() {
        return game;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Player getWinner() {
        return winner;
    }

    public List<Score> getScores() {
        return scores;
    }
}
