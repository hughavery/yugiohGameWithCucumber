package uc.seng301.cardbattler.asg3.cards;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uc.seng301.cardbattler.asg3.model.Card;

/**
 * Card generation proxy for getting random card from API
 */
public class CardProxy implements CardGenerator {
    private final CardService cardService;
    private static final Logger LOGGER = LogManager.getLogger(CardProxy.class);

    /**
     * Create a new Card proxy using the {@link CardService} implementation
     */
    public CardProxy() {
        this.cardService = new CardService();
    }

    @Override
    public Card getRandomCard() {
        return getRandomCardOfType(CardType.RANDOM);
    }

    @Override
    public Card getRandomCardOfType(CardType cardType) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // this lambda is somewhat equivalent to using a Thread.sleep(), but more
        // elegant, the call to get a random card will be delayed by one second
        try {
            return scheduler.schedule(() -> cardService.getRandomCardOfType(cardType), 1, TimeUnit.SECONDS)
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error("Unable to get a random card. Reason: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
