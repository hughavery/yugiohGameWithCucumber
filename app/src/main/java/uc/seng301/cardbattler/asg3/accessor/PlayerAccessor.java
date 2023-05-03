package uc.seng301.cardbattler.asg3.accessor;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import uc.seng301.cardbattler.asg3.model.Player;

/**
 * This class offers helper methods for saving, removing, and fetching players
 * from persistence
 * 
 * @see {@link Player} entities
 */
public class PlayerAccessor {
    private static final Logger LOGGER = LogManager.getLogger(PlayerAccessor.class);
    private final SessionFactory sessionFactory;

    /**
     * default constructor
     * 
     * @param sessionFactory the JPA session factory to talk to the persistence
     *                       implementation.
     */
    public PlayerAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Player} object with the given parameters
     * 
     * @param name The Player name must be [a-zA-Z0-9] (not null, empty, or only
     *             numerics)
     * @return The Player object with given parameters
     * @throws IllegalArgumentException If any of the above preconditions for input
     *                                  arguments are violated
     */
    public Player createPlayer(String name) throws IllegalArgumentException {
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (name.matches("\\d+") || !name.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Player name be alphanumerical but cannot only be numeric");
        }
        Player player = new Player();
        player.setName(name);
        player.setDecks(new ArrayList<>());
        return player;
    }

    /**
     * Gets player from persistence by name
     * 
     * @param name name of player to fetch
     * @return Player with given name
     */
    public Player getPlayerByName(String name) {
        if (null == name || name.isBlank()) {
            throw new IllegalArgumentException("name '" + name + "' cannot be null or blank");
        }
        Player player = null;
        try (Session session = sessionFactory.openSession()) {
            player = session.createQuery("FROM Player where name='" + name + "'", Player.class).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return player;
    }

    /**
     * Gets player from persistence by id
     * 
     * @param playerId id of player to fetch
     * @return Player with given id
     */
    public Player getPlayerById(Long playerId) {
        if (null == playerId) {
            throw new IllegalArgumentException("cannot retrieve player with null id");
        }
        Player player = null;
        try (Session session = sessionFactory.openSession()) {
            player = session.createQuery("FROM Player WHERE playerId =" + playerId, Player.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
        }
        return player;
    }

    /**
     * Saves player to persistence
     * 
     * @param player player to save
     * @return id of saved player, or -1 if an error occurred
     * @throws IllegalArgumentException if player object is invalid (e.g. missing
     *                                  properties)
     */
    public Long persistPlayer(Player player) throws IllegalArgumentException {
        if (null == player || null == player.getName() || player.getName().isBlank()) {
            throw new IllegalArgumentException("cannot save null or blank player");
        }

        Player existingPlayer = getPlayerByName(player.getName());
        if (null != existingPlayer) {
            player.setPlayerId(existingPlayer.getPlayerId());
            return existingPlayer.getPlayerId();
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to run transaction", e);
            return -1L;
        }
        return player.getPlayerId();
    }

    /**
     * remove given player from persistence by id
     * 
     * @param playerId id of player to be deleted
     * @return true if the record is deleted
     */
    public boolean removePlayerById(Long playerId) {
        Player player = getPlayerById(playerId);
        if (null != player) {
            try (Session session = sessionFactory.openSession()) {
                Transaction transaction = session.beginTransaction();
                session.remove(player);
                transaction.commit();
                return true;
            } catch (HibernateException e) {
                LOGGER.error("Unable to run transaction", e);
            }
        }
        return false;
    }
}
