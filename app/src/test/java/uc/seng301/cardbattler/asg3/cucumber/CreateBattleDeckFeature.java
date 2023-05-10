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
import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class CreateBattleDeckFeature {
    private SessionFactory sessionFactory;
    private Player player;
    private PlayerAccessor playerAccessor;
    private Deck deck;
    private Deck battleDeck;
    private BattleDeckCreator battleDeckCreator;
    private DeckAccessor deckAccessor;
    private CardService cardGeneratorSpy;
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
        cardGeneratorSpy = Mockito.spy(new CardService());
        cli = Mockito.mock(CommandLineInterface.class);
        game = new Game(cardGeneratorSpy, cli, sessionFactory);
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
        player = playerAccessor.createPlayer(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(player.getName(), name);
    }

    @Given("There is no deck named {string}")
    public void there_is_no_deck_named(String name) {
        deck = deckAccessor.getDeckByName(name);
        Assertions.assertNull(deck);
    }

    @When("I create a battle deck named {string}")
    public void i_create_a_battle_deck_named(String battleDeckName) {
        addInputMocking("random");
        game.battleDeck("battle_deck " + player.getName() + " " + battleDeckName);
        battleDeck = deckAccessor.getDeckByName(battleDeckName);
        Assertions.assertNotNull(battleDeck);
        Assertions.assertEquals(battleDeck.getName(), battleDeckName);
    }

    @When("I create a battle deck named {string} with {int} monsters, {int} spells and {int} traps")
    public void i_create_a_battle_deck_named_with_monsters_spells_and_traps(String battleDeckName, Integer numMonsters, Integer numSpells, Integer numTraps) {
        addInputMocking("choice",numMonsters.toString(),numSpells.toString(),numTraps.toString());
        game.battleDeck("battle_deck " + player.getName() + " " + battleDeckName);
        battleDeck = deckAccessor.getDeckByName("MyFirstBattleDeck");
        Assertions.assertNotNull(battleDeck);
        Assertions.assertEquals(battleDeck.getName(), battleDeckName);
    }

    @Then("The battle deck must contain {int} cards exactly")
    public void the_battle_deck_must_contain_cards_exactly(Integer SizeOfDeck) {
        battleDeck = deckAccessor.getDeckByName("MyFirstBattleDeck");
        int battleDeckSize = battleDeck.getCards().size();
        Assertions.assertEquals(battleDeckSize,SizeOfDeck);
    }


    @Then("The battle deck contains at least {int} monsters")
    public void the_battle_deck_contains_at_least_monsters(Integer numOfMonsters) {
        // find out how many mosters in battle deck
        int monsterCount = 0;
        List<Card> battleCardsList = battleDeck.getCards();
        for (int i = 0; i < battleCardsList.size(); i++) {
            if (battleCardsList.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("monster")){
                monsterCount ++;
            }
        }
        Assertions.assertTrue(monsterCount >= numOfMonsters);
    }
    @Then("The battle deck contains at least {int} spells")
    public void the_battle_deck_contains_at_least_spells(Integer numOfSpells) {
        int spellCount = 0;
        List<Card> battleCardsList = battleDeck.getCards();
        for (int i = 0; i < battleCardsList.size(); i++) {
            if (battleCardsList.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("spell")) {
                spellCount ++;
            }
        }
        Assertions.assertTrue(spellCount >= numOfSpells);
    }
    @Then("The battle deck contains at least {int} traps")
    public void the_battle_deck_contains_at_least_traps(Integer numOfTraps) {
        int trapCount = 0;
        List<Card> battleCardsList = battleDeck.getCards();
        for (int i = 0; i < battleCardsList.size(); i++) {
            if (battleCardsList.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("trap")) {
                trapCount ++;
            }
        }
        Assertions.assertTrue(trapCount >= numOfTraps);
    }

}
