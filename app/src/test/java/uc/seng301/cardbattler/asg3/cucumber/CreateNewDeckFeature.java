package uc.seng301.cardbattler.asg3.cucumber;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.cardbattler.asg3.accessor.CardAccessor;
import uc.seng301.cardbattler.asg3.accessor.DeckAccessor;
import uc.seng301.cardbattler.asg3.accessor.PlayerAccessor;
import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.CardPosition;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Monster;
import uc.seng301.cardbattler.asg3.model.Player;

public class CreateNewDeckFeature {
    private PlayerAccessor playerAccessor;
    private DeckAccessor deckAccessor;
    private CardAccessor cardAccessor;

    private Player player;
    private Card card;
    private Deck deck;

    private Exception expectedException;

    private String deckName;

    @Before
    public void setup() {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        deckAccessor = new DeckAccessor(sessionFactory);
        cardAccessor = new CardAccessor(sessionFactory);
    }

    @Given("A player named {string} exists")
    public void a_player_named_exists(String name) {
        player = playerAccessor.createPlayer(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(player.getName(), name);
    }

    @Given("I have a card {string} with attack {int}, defence {int}, and life {int}")
    public void i_have_a_card_with_attack_defence_and_life(String name, int attack, int defence, int life) {
        card = cardAccessor.createMonster(name, attack, defence, life, CardPosition.ATTACK);
        Long cardId = cardAccessor.persistCard(card);
        Assertions.assertNotNull(card);
        Assertions.assertNotNull(cardId);
        Assertions.assertSame(card.getName(), name);
        Monster monsterCard = (Monster) card;
        Assertions.assertEquals(monsterCard.getAttack(), attack);
        Assertions.assertEquals(monsterCard.getDefence(), defence);
        Assertions.assertEquals(monsterCard.getLife(), life);
    }

    @Given("The player has no decks with the name {string}")
    public void the_player_has_no_decks_with_the_name(String name) {
        Assertions.assertEquals(0, player.getDecks().stream().filter(d -> d.getName().equals(name)).toList().size());
    }

    @Given("The player has a deck with the name {string} and a card")
    public void the_player_has_a_deck_with_the_name(String name) {
        Deck _deck = new Deck();
        _deck.setName(name);
        player.getDecks().add(_deck);
        Assertions.assertEquals(1, player.getDecks().stream().filter(d -> d.getName().equals(name)).toList().size());
    }

    @When("I create a deck {string} with a card")
    public void i_create_a_deck_with_a_card(String name) {
        deckName = name;
        deck = deckAccessor.createDeck(name, player, List.of(card));
        deckAccessor.persistDeck(deck);
    }

    @When("I create an invalid deck {} with a card")
    public void i_create_an_invalid_deck_with_a_card(String name) {
        List<Card> cards = List.of(card);
        expectedException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> deckAccessor.createDeck(name, player, cards));
    }

    @When("I create a deck {string} without a card")
    public void i_create_a_deck_without_a_card(String name) {
        deckName = name;
        deck = deckAccessor.createDeck(name, player, new ArrayList<>());
        deckAccessor.persistDeck(deck);

    }

    @Then("The deck is created for the player with the correct name and card")
    public void the_deck_is_created_for_the_player_with_the_correct_name_and_card() {
        Assertions.assertNotNull(deck);
        Assertions.assertEquals(deck.getName(), deckName);
        Assertions.assertSame(deck.getPlayer(), player);
        Assertions.assertTrue(deck.getCards().contains(card));
    }

    @Then("The deck is created for the player with the correct name and no cards")
    public void the_deck_is_created_for_the_player_with_the_correct_name_and_no_cards() {
        Assertions.assertNotNull(deck);
        Assertions.assertEquals(deck.getName(), deckName);
        Assertions.assertSame(deck.getPlayer(), player);
        Assertions.assertTrue(deck.getCards().isEmpty());
    }

    @Then("I am not allowed to create the deck")
    public void i_am_not_allowed_to_create_the_deck() {
        Assertions.assertNotNull(expectedException);
    }
}
