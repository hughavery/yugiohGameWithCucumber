package uc.seng301.cardbattler.asg3.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.CardPosition;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Monster;
import uc.seng301.cardbattler.asg3.model.Spell;
import uc.seng301.cardbattler.asg3.model.Trap;

/**
 * Represents 1 side of the game board of a Yu-Gi-Oh! game
 * Stores the deck, hand, and different board card slots
 */
public class GameBoard {
    private static final int INITIAL_HAND_SIZE = 5;
    private static final int CARDS_TO_DRAW_PER_TURN = 1;
    private final List<Monster> monsterSlots;
    private final List<Spell> spellSlots;
    private final List<Trap> trapSlots;
    private final List<Card> battleCards;
    private final List<Card> hand = new ArrayList<>();

    /**
     * Create a new GameBoard with the given deck
     * 
     * @param deck Deck to be used for a game, the cards of this deck will be copied
     */
    public GameBoard(Deck deck) {
        monsterSlots = new ArrayList<>();
        spellSlots = new ArrayList<>();
        trapSlots = new ArrayList<>();
        this.battleCards = new Deck(deck).getCards();
    }

    /**
     * Plays a card from your hand (The card requested must exist in the current
     * hand
     * 
     * @param card Card to play (must exist in hand)
     */
    public void playCard(Card card) {
        if (!hand.remove(card)) {
            return;
        }
        if (card instanceof Monster monster) {
            if (CardPosition.ATTACK.equals(monster.getCardPosition())) {
                monster.setLife(monster.getAttack());
            } else {
                monster.setLife(monster.getDefence());
            }
            monsterSlots.add(monster);
        } else if (card instanceof Spell spell) {
            spellSlots.add(spell);
        } else if (card instanceof Trap trap) {
            trapSlots.add(trap);
        }

    }

    /**
     * Gets the current hand
     * 
     * @return Cards in current hand
     */
    public List<Card> getHand() {
        return hand;
    }

    /**
     * Handles operations at the start of a turn
     * Currently only draws a set number of cards
     */
    public void startTurn() {
        for (int i = 0; i < CARDS_TO_DRAW_PER_TURN; i++) {
            hand.add(battleCards.remove(0));
        }
    }

    /**
     * Handles operations at the start of a game
     * Currently only draws a set number of cards
     */
    public void startGame() {
        Collections.shuffle(battleCards);
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            hand.add(battleCards.remove(0));
        }
    }

    /**
     * Gets all the monsters on the board
     * 
     * @return monsters on the board
     */
    public List<Monster> getMonsterSlots() {
        return monsterSlots;
    }

    /**
     * Gets all the spells on the board
     * 
     * @return spells on the board
     */
    public List<Spell> getSpellSlots() {
        return spellSlots;
    }

    /**
     * Gets all the traps on the board
     * 
     * @return traps on the board
     */
    public List<Trap> getTrapSlots() {
        return trapSlots;
    }
}
