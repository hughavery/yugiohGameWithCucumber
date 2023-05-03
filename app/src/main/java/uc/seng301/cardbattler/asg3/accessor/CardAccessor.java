package uc.seng301.cardbattler.asg3.accessor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.CardPosition;
import uc.seng301.cardbattler.asg3.model.Monster;

/**
 * This class offers helper methods for saving, removing, and fetching card
 * records from persistence
 * 
 * @see {@link Card} entities
 */
public class CardAccessor {
    private static final Logger LOGGER = LogManager.getLogger(CardAccessor.class);
    private final SessionFactory sessionFactory;

    /**
     * default constructor
     * 
     * @param sessionFactory the JPA session factory to talk to the persistence
     *                       implementation.
     */
    public CardAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Card} object with the given parameters
     * 
     * @param name    the card name (not null, empty, or only numerics, can be
     *                [a-zA-Z0-9 ])
     * @param attack  attack value of card (must be > 0)
     * @param defence defence value of card (must be > 0)
     * @param life    life value of card (must be >= 0)
     * @return The Card object with given parameters
     * @throws IllegalArgumentException if any of the above preconditions for input
     *                                  arguments are violated
     */
    public Card createMonster(String name, int attack, int defence, int life, CardPosition cardPosition) {
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (name.matches("\\d+") || !name.matches("[a-zA-Z0-9 ]+")) {
            throw new IllegalArgumentException("Name must be alphanumerical but cannot only be numeric");
        }
        if (attack <= 0) {
            throw new IllegalArgumentException("Attack must be strictly positive");
        }
        if (defence <= 0) {
            throw new IllegalArgumentException("Defence must be strictly positive");
        }
        if (life < 0) {
            throw new IllegalArgumentException("Life cannot be negative");
        }
        Monster card = new Monster();
        card.setName(name);
        card.setAttack(attack);
        card.setDefence(defence);
        card.setLife(life);
        card.setCardPosition(cardPosition);
        return card;
    }

    /**
     * Get game object from persistence layer
     * 
     * @param cardId id of card to fetch
     * @return Card with id given if it exists in database, null if not found
     * @throws IllegalArgumentException if passed id is null
     */
    public Card getCardById(Long cardId) {
        if (null == cardId) {
            throw new IllegalArgumentException("cannot retrieve card with null id");
        }
        Card card = null;
        try (Session session = sessionFactory.openSession()) {
            card = session.createQuery("FROM card WHERE cardId =" + cardId, Card.class).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return card;
    }

    /**
     * Get a card by its name
     * 
     * @param cardName a name to look for
     * @return the Card with given name if such card exists, null if not found
     * @throws IllegalArgumentException if passed name is null
     */
    public Card getCardByName(String cardName) {
        if (null == cardName) {
            throw new IllegalArgumentException("cannot retrieve card with null name");
        }
        Card card = null;
        try (Session session = sessionFactory.openSession()) {
            card = session.createQuery("FROM card WHERE name ='" + cardName + "'", Card.class).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return card;
    }

    /**
     * Gets all cards belonging to player by id
     * 
     * @param deckId id of player to fetch cards
     * @return Cards belonging to player with id provided, empty list if either not
     *         found or an error occurred
     * @throws IllegalArgumentException if passed deckId is null
     */
    public List<Card> getDeckCardsById(Long deckId) {
        if (null == deckId) {
            throw new IllegalArgumentException("cannot retrieve card with null id");
        }
        List<Card> cards = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            cards = session.createQuery("FROM card WHERE deckId=" + deckId, Card.class).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return cards;
    }

    /**
     * Save given card to persistence
     * 
     * @param card card to be saved (a Card is always well formed)
     * @return The id of the persisted card, -1 if an error occurred
     */
    public Long persistCard(Card card) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(card);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
            return -1L;
        }
        return card.getCardId();
    }

    /**
     * remove given card from persistence by id
     * 
     * @param cardId id of card to be deleted
     * @return true if the record is deleted
     */
    public boolean deleteCardById(Long cardId) throws IllegalArgumentException {
        Card card = getCardById(cardId);
        if (null != card) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.remove(card);
                transaction.commit();
                return true;
            } catch (HibernateException e) {
                LOGGER.error("Unable to run transaction", e);
            }
        }
        return false;
    }

}
