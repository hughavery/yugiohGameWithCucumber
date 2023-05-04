package uc.seng301.cardbattler.asg3.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import uc.seng301.cardbattler.asg3.accessor.DeckAccessor;
import uc.seng301.cardbattler.asg3.accessor.PlayerAccessor;
import uc.seng301.cardbattler.asg3.cards.CardService;
import uc.seng301.cardbattler.asg3.cli.CommandLineInterface;
import uc.seng301.cardbattler.asg3.game.BattleDeckCreator;
import uc.seng301.cardbattler.asg3.game.Game;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;

public class CreateBattleDeckFeature {
    private SessionFactory sessionFactory;
    private Player player;
    private PlayerAccessor playerAccessor;
    private Deck deck;
    private Deck battleDeck;
    private BattleDeckCreator battleDeckCreator;
    private DeckAccessor deckAccessor;
    private CommandLineInterface cli;
    private Game game;

    @Before
    public void setup() {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Configuration configuration = new Configuration();
        configuration.configure();
        sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        deckAccessor = new DeckAccessor(sessionFactory);
//        cardGeneratorSpy = Mockito.spy(new CardService());
        cli = Mockito.mock(CommandLineInterface.class);
        // custom printer for debugging purposes
        Mockito.doAnswer((i) -> {
            System.out.println((String) i.getArgument(0));
            return null;
        }).when(cli).printLine(Mockito.anyString());
    }

    private void addInputMocking(String... mockedInputs) {
        Iterator<String> toMock = Arrays.asList(mockedInputs).iterator();
        Mockito.when(cli.getNextLine()).thenAnswer(i -> toMock.next());
    }

    @Given("The player {string} exists")
    public void the_player_exists(String name) {
        player = playerAccessor.getPlayerByName(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(player.getPlayerId(), playerId);
    }

    @Given("There is no deck named {string}")
    public void there_is_no_deck_named(String name) {
        deck = deckAccessor.getDeckByName(name);
        Assertions.assertNull(deck);
    }

    @When("I create a random battle deck named {string}")
    public void i_create_a_random_battle_deck_named(String battleDeckName) {
//        battleDeck =
        addInputMocking("random");
        game.battleDeck("battle_deck " + player.getName() + " " + battleDeckName);
        battleDeck = deckAccessor.getDeckByName(battleDeckName);


    }

    @Then("The battle deck must contain {int} cards exactly")
    public void the_battle_deck_must_contain_cards_exactly(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
