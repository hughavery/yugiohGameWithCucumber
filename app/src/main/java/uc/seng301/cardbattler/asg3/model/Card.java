package uc.seng301.cardbattler.asg3.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * A Card entity stores the data for a game card.
 * This entity should not be manipulated from this JPA class, but always through
 * its accessor class
 * 
 * @see {@link uc.seng301.cardbattler.asg3.accessor.CardAccessor}
 */
@Entity(name = "card")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "TINYINT(1)")
public abstract class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_card")
    private Long cardId;

    private String name;
    private String description;

    protected Card() {
        // a public constructor is needed by JPA
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", name='" + name + '\'' +
                ", description=" + getCardDescription() +
                '}';
    }

    /**
     * Get human-readable card description to display in terminal
     * 
     * @return human-readable card description
     */
    public abstract String getCardDescription();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Card card))
            return false;
        return name.equals(card.name) && description.equals(card.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}
