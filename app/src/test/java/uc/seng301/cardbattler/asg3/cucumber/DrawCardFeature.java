package uc.seng301.cardbattler.asg3.cucumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.cardbattler.asg3.accessor.DeckAccessor;
import uc.seng301.cardbattler.asg3.accessor.PlayerAccessor;
import uc.seng301.cardbattler.asg3.cards.CardGenerator;
import uc.seng301.cardbattler.asg3.cards.CardService;
import uc.seng301.cardbattler.asg3.cli.CommandLineInterface;
import uc.seng301.cardbattler.asg3.game.Game;
import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Monster;
import uc.seng301.cardbattler.asg3.model.Player;

public class DrawCardFeature {
    private SessionFactory sessionFactory;
    private PlayerAccessor playerAccessor;
    private DeckAccessor deckAccessor;
    private CardService cardGeneratorSpy;
    private Player player;
    private Card card;
    private Deck deck;

    private Game game;
    private CommandLineInterface cli;

    @Before
    public void setup() {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        deckAccessor = new DeckAccessor(sessionFactory);
        cardGeneratorSpy = Mockito.spy(new CardService());
        cli = Mockito.mock(CommandLineInterface.class);
        // custom printer for debugging purposes
        Mockito.doAnswer((i) -> {
            System.out.println((String) i.getArgument(0));
            return null;
        }).when(cli).printLine(Mockito.anyString());
    }

    /**
     * Adds any number of strings to input mocking FIFO
     * You may find this helpful for U4
     * 
     * @param mockedInputs strings to add
     */
    private void addInputMocking(String... mockedInputs) {
        Iterator<String> toMock = Arrays.asList(mockedInputs).iterator();
        Mockito.when(cli.getNextLine()).thenAnswer(i -> toMock.next());
    }

    @Given("I have a player {string}")
    public void i_have_a_player(String name) {
        player = playerAccessor.createPlayer(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(name, player.getName());
    }

    @Given("I have an empty deck {string}")
    public void i_have_an_empty_deck(String name) {
        deck = deckAccessor.createDeck(name, player, new ArrayList<>());
        Long deckId = deckAccessor.persistDeck(deck);
        Assertions.assertNotNull(deck);
        Assertions.assertNotNull(deckId);
        Assertions.assertSame(name, deck.getName());
        Assertions.assertTrue(deck.getCards().isEmpty());
    }

    @When("I draw a randomly selected card")
    public void i_draw_a_randomly_selected_card() {
        String apiResponse = "{\"data\":[{\"id\":44073668,\"name\":\"Takriminos\",\"type\":\"Normal Monster\",\"frameType\":\"normal\",\"desc\":\"A member of a race of sea serpents that freely travels through the sea.\",\"atk\":1500,\"def\":1200,\"level\":4,\"race\":\"Sea Serpent\",\"attribute\":\"WATER\"}]}\n";
        Mockito.doReturn(apiResponse).when(cardGeneratorSpy).getResponseFromAPI(Mockito.any());
        card = ((CardGenerator) cardGeneratorSpy).getRandomCard();
        Assertions.assertNotNull(card);
    }

    @When("While playing I draw a randomly selected card")
    public void while_playing_i_draw_a_randomly_selected_card() {
        String apiResponse = "{\"data\":[{\"id\":44073668,\"name\":\"Takriminos\",\"type\":\"Normal Monster\",\"frameType\":\"normal\",\"desc\":\"A member of a race of sea serpents that freely travels through the sea.\",\"atk\":1500,\"def\":1200,\"level\":4,\"race\":\"Sea Serpent\",\"attribute\":\"WATER\"}]}\n";
        Mockito.doReturn(apiResponse).when(cardGeneratorSpy).getResponseFromAPI(Mockito.any());
        game = new Game(cardGeneratorSpy, cli, sessionFactory);
        Assertions.assertNotNull(game);

    }

    @Then("I receive a random monster card with valid stats")
    public void i_receive_a_random_monster_card_with_valid_stats() {
        Assertions.assertNotNull(card);
        Assertions.assertNotNull(card.getName());
        Monster monsterCard = (Monster) card;
        Assertions.assertTrue(monsterCard.getAttack() > 0);
        Assertions.assertTrue(monsterCard.getDefence() > 0);
        Assertions.assertTrue(monsterCard.getLife() >= 0);
    }

    @When("I decide I want to add the card to my deck")
    public void i_decide_i_want_to_add_the_card_to_my_deck() {
        addInputMocking("Y");
        game.draw("draw " + player.getName() + " " + deck.getName());
    }

    @Then("The card is added to my deck")
    public void the_card_is_added_to_my_deck() {
        deck = deckAccessor.getDeckByName(deck.getName());
        Assertions.assertEquals(1, deck.getCards().size());
    }

    @When("I decide I want to ignore the card")
    public void i_decide_i_want_to_ignore_the_card() {
        addInputMocking("N");
        game.draw("draw " + player.getName() + " " + deck.getName());
    }

    @Then("The card is not added to my deck")
    public void the_card_is_not_added_to_my_deck() {
        deck = deckAccessor.getDeckByName(deck.getName());
        Assertions.assertEquals(0, deck.getCards().size());
    }

}
