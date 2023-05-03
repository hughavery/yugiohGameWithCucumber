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
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Player;

/**
 * This class offers helper methods for saving, removing, and fetching deck
 * records from persistence
 * 
 * @see {@link Deck} entities
 */
public class DeckAccessor {
    private static final Logger LOGGER = LogManager.getLogger(DeckAccessor.class);
    private final SessionFactory sessionFactory;

    /**
     * default constructor
     *
     * @param sessionFactory the JPA session factory to talk to the persistence
     *                       implementation.
     */
    public DeckAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Deck} object with the given parameters
     *
     * @param name   The Deck name must be [a-zA-Z0-9 ] (not null, empty, or only
     *               numerics)
     * @param player The Player whose deck it is (cannot be null)
     * @param cards  The cards to be in the deck, must not be null and must have at
     *               least 1 card
     * @return The Deck object with given parameters
     * @throws IllegalArgumentException If any of the above preconditions for input
     *                                  arguments are violated
     */
    public Deck createDeck(String name, Player player, List<Card> cards) throws IllegalArgumentException {
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Deck name cannot be empty.");
        }
        if (name.matches("\\d+") || !name.matches("[a-zA-Z0-9 ]+")) {
            throw new IllegalArgumentException("Deck name be alphanumerical but cannot only be numeric");
        }
        if (player == null) {
            throw new IllegalArgumentException("Deck must be associated with a player");
        }
        if (cards == null) {
            throw new IllegalArgumentException("Deck must not be null");
        }
        if (!player.getDecks().stream().filter(d -> d.getName().equals(name)).toList().isEmpty()) {
            throw new IllegalArgumentException("Player already has a deck named: " + name);
        }
        Deck deck = new Deck();
        deck.setPlayer(player);
        deck.setName(name);
        deck.setCards(cards);
        return deck;
    }

    /**
     * Get deck from persistence layer
     *
     * @param deckId id of deck to fetch
     * @return Deck with id given if it exists in database, null if not found
     */
    public Deck getDeckById(Long deckId) {
        if (null == deckId) {
            throw new IllegalArgumentException("cannot retrieve deck with null id");
        }
        Deck deck = null;
        try (Session session = sessionFactory.openSession()) {
            deck = session.createQuery("FROM Deck WHERE deckId =" + deckId, Deck.class).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return deck;
    }

    /**
     * Get deck from persistence layer by name
     *
     * @param name name of deck to fetch
     * @return Deck with name given if it exists in database, null if not found
     */
    public Deck getDeckByName(String name) {
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("cannot retrieve deck with null or empty name");
        }
        Deck deck = null;
        try (Session session = sessionFactory.openSession()) {
            deck = session.createQuery("FROM Deck WHERE name='" + name + "'", Deck.class).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return deck;
    }

    /**
     * Get Deck from persistence layer by player name and deck name
     *
     * @param playerName name of player who owns the deck
     * @param deckName   name of deck
     * @return The matching deck
     */
    public Deck getDeckByPlayerNameAndDeckName(String playerName, String deckName) {
        if (null == playerName || playerName.isEmpty()) {
            throw new IllegalArgumentException("cannot retrieve deck with null or empty player name");
        }
        if (null == deckName || deckName.isEmpty()) {
            throw new IllegalArgumentException("cannot retrieve deck with null or empty player name");
        }
        Deck deck = null;
        try (Session session = sessionFactory.openSession()) {
            Player player = session.createQuery("FROM Player WHERE name='" + playerName + "'", Player.class)
                    .uniqueResult();
            deck = session
                    .createQuery("FROM Deck WHERE name='" + deckName + "' AND player=" + player.getPlayerId(),
                            Deck.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return deck;
    }

    /**
     * Gets all decks belonging to player by id
     *
     * @param playerId id of player to fetch decks
     * @return Decks belonging to player with id provided, empty list if not found
     *         or an error occurred
     */
    public List<Deck> getPlayerDecksById(Long playerId) {
        List<Deck> decks = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            decks = session.createQuery("FROM Deck WHERE playerId=" + playerId, Deck.class).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return decks;
    }

    /**
     * Save given deck to persistence
     *
     * @param deck deck to be saved
     * @return The id of the persisted deck, -1 if an error occurred
     */
    public Long persistDeck(Deck deck) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(deck);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
            return -1L;
        }
        return deck.getDeckId();
    }

    /**
     * Update given deck, must already exist in persistence
     *
     * @param deck deck to updated, must be a valid Deck.
     * @return The id of the updated deck
     */
    public long updateDeck(Deck deck) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(deck);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return deck.getDeckId();
    }

    /**
     * remove given deck from persistence by id
     *
     * @param deckId id of deck to be deleted
     * @return true if the record is deleted, false if an error occurred or the deck
     *         does not exist
     */
    public boolean deleteDeckById(Long deckId) throws IllegalArgumentException {
        Deck deck = getDeckById(deckId);
        if (null != deck) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.remove(deck);
                transaction.commit();
                return true;
            } catch (HibernateException e) {
                LOGGER.error("Unable to run transaction", e);
            }
        }
        return false;
    }
}
