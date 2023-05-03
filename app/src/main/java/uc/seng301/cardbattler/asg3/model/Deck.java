package uc.seng301.cardbattler.asg3.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * A Deck entity stores the data for a game deck.
 * This entity should not be manipulated from this JPA class, but always through
 * its accessor class
 * 
 * @see {@link uc.seng301.cardbattler.asg3.accessor.DeckAccessor}
 */
@Entity
@Table(name = "deck")
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_deck")
    private Long deckId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_player")
    private Player player;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_deck")
    private List<Card> cards;

    private String name;

    /**
     * Default empty constructor
     */
    public Deck() {
        // a (public) constructor is needed by JPA
    }

    /**
     * Copy constructor, the player will be referenced, not deep copied. Cards will
     * be copied (i.e. cloned)
     * 
     * @param deck a deck to copy
     */
    public Deck(Deck deck) {
        setDeckId(deck.getDeckId());
        setName(deck.getName());
        setPlayer(deck.getPlayer());
        setCards(new ArrayList<>());
        cards = new ArrayList<>();
        deck.getCards().forEach(card -> {
            if (card instanceof Monster monster) {
                cards.add(new Monster(monster));
            } else if (card instanceof Spell spell) {
                cards.add(new Spell(spell));
            } else {
                cards.add(new Trap((Trap) card));
            }
        });
    }

    public Long getDeckId() {
        return deckId;
    }

    public void setDeckId(Long deckId) {
        this.deckId = deckId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    /**
     * Adds any number of cards to the deck
     * 
     * @param cards cards to add
     */
    public void addCards(Card... cards) {
        this.cards.addAll(Arrays.asList(cards));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Deck (" + getDeckId() + ") named: " + getName() + ", with cards:\n");
        getCards().forEach(card -> builder.append("\t" + card.toString() + "\n"));
        return builder.toString();
    }
}
