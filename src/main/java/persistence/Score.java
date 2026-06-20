package persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** A single player's score within a round. */
@Entity
@Table(name = "score")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(nullable = false)
    private int points;

    protected Score() {
        // required by JPA
    }

    public Score(Player player, int points) {
        this.player = player;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    void setRound(Round round) {
        this.round = round;
    }

    public Round getRound() {
        return round;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPoints() {
        return points;
    }
}
