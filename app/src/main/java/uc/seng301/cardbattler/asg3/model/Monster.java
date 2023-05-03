package uc.seng301.cardbattler.asg3.model;

import java.util.Objects;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * This entity is a special type of {@link Card}
 */
@Entity
@Table(name = "monster")
@DiscriminatorValue("1")
public class Monster extends Card {
    private int attack;
    private int defence;
    private int life;
    private CardPosition cardPosition;

    /**
     * Empty default constructor
     */
    public Monster() {
        // empty for JPA
    }

    /**
     * Copy constructor
     * 
     * @param monster a monster to copy
     */
    public Monster(Monster monster) {
        setCardId(monster.getCardId());
        setName(monster.getName());
        setDescription(monster.getDescription());
        setCardPosition(monster.getCardPosition());
        setAttack(monster.getAttack());
        setDefence(monster.getDefence());
        setLife(monster.getLife());
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public CardPosition getCardPosition() {
        return cardPosition;
    }

    public void setCardPosition(CardPosition cardPosition) {
        this.cardPosition = cardPosition;
    }

    @Override
    public String getCardDescription() {
        return String.format("Monster -- %s -- Atk: %d Def: %d Life: %d -- Currently: %s", getName(), attack, defence,
                life,
                cardPosition.label);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Monster)) {
            return false;
        }
        Monster monster = (Monster) o;
        return attack == monster.attack && defence == monster.defence && life == monster.life
                && Objects.equals(cardPosition, monster.cardPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attack, defence, life, cardPosition);
    }

}
