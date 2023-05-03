package uc.seng301.cardbattler.asg3.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.CardPosition;
import uc.seng301.cardbattler.asg3.model.Monster;
import uc.seng301.cardbattler.asg3.model.Spell;
import uc.seng301.cardbattler.asg3.model.Trap;

/**
 * {@link Card} API response JSON deserializer
 */
public class CardResponse {
    @JsonDeserialize
    @JsonProperty("name")
    private String name;

    @JsonDeserialize
    @JsonProperty("race")
    private String race;

    @JsonDeserialize
    @JsonProperty("atk")
    private int attack;

    @JsonDeserialize
    @JsonProperty("def")
    private int defence;

    @JsonDeserialize
    @JsonProperty("level")
    private int level;

    @JsonDeserialize
    @JsonProperty("type")
    private String type;

    @JsonDeserialize
    @JsonProperty("attribute")
    private String attribute;

    @JsonDeserialize
    @JsonProperty("desc")
    private String description;

    public CardResponse() {
        // no-args jackson constructor
    }

    @Override
    public String toString() {
        return "CardResponse{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", race='" + race + '\'' +
                ", attribute='" + attribute + '\'' +
                ", attack=" + attack +
                ", defence=" + defence +
                ", level=" + level +
                '}';
    }

    /**
     * Converts itself to a Card
     * 
     * @return Card representation of json deserialized response
     */
    public Card toCard() {
        if (type.toLowerCase().contains("monster")) {
            Monster card = new Monster();
            card.setName(name);
            card.setDescription(description);
            card.setAttack(attack);
            card.setDefence(defence);
            card.setLife(0);
            card.setCardPosition(CardPosition.ATTACK);
            return card;
        } else if (type.toLowerCase().contains("spell")) {
            Spell card = new Spell();
            card.setName(name);
            card.setDescription(description);
            return card;
        } else if (type.toLowerCase().contains("trap")) {
            Trap card = new Trap();
            card.setName(name);
            card.setDescription(description);
            return card;
        }
        return null;
    }

}
