package uc.seng301.cardbattler.asg3.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This entity is a special type of {@link Card}
 */
@Entity
@Table(name = "spell")
@DiscriminatorValue("2")
public class Spell extends Card {

    /**
     * Empty JPA compliant constructor
     */
    public Spell() {
        // empty for JPA
    }

    /**
     * Copy constructor
     * 
     * @param spell a spell to copy
     */
    public Spell(Spell spell) {
        setCardId(spell.getCardId());
        setName(spell.getName());
        setDescription(spell.getDescription());
    }

    @Override
    public String getCardDescription() {
        return String.format("Spell -- %s -- %s", getName(), getDescription());
    }

}
