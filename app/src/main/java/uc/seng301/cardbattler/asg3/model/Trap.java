package uc.seng301.cardbattler.asg3.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This entity is a special type of {@link Card}
 */
@Entity
@Table(name = "trap")
@DiscriminatorValue("3")
public class Trap extends Card {

    /**
     * Empty JPA compliant constructor
     */
    public Trap() {
        // empty for JPA
    }

    /**
     * Copy constructor
     * 
     * @param trap a trap to copy
     */
    public Trap(Trap trap) {
        setCardId(trap.getCardId());
        setName(trap.getName());
        setDescription(trap.getDescription());
    }

    @Override
    public String getCardDescription() {
        return String.format("Trap -- %s -- %s", getName(), getDescription().replace("\n", "\t"));
    }
}
