package uc.seng301.cardbattler.asg3.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * A Player entity stores the data for a game player.
 * This entity should not be manipulated from this JPA class, but always through
 * its accessor class
 * 
 * @see {@link uc.seng301.cardbattler.asg3.accessor.PlayerAccessor}
 */
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_player")
    private Long playerId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private List<Deck> decks;

    private String name;

    public Player() {
        // no-args constructor needed by JPA
    }

    /* Getters and setters omitted */

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void setDecks(List<Deck> decks) {
        this.decks = decks;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Player (%d): %s, Decks:%n", playerId, name));
        for (Deck deck : decks) {
            sb.append("  " + deck.toString());
        }
        return sb.toString();
    }
}
