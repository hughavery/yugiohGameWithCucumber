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
import uc.seng301.cardbattler.asg3.game.GameBoard;
import uc.seng301.cardbattler.asg3.model.*;

import java.util.*;
import java.util.logging.Level;

public class CreateGameBoardFeature {
    private SessionFactory sessionFactory;
    private Player player;
    private PlayerAccessor playerAccessor;
    private Deck deck;
    private Deck battleDeck;
    private BattleDeckCreator battleDeckCreator;
    private DeckAccessor deckAccessor;
    private GameBoard gameBoard;
    private List<Card> PlayersHand;
    private int monsterCount = 0;
    private int spellCount = 0;
    private int trapCount = 0;
    private String playeBattleDeckName;
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

    @Given("The player with name {string} exists")
    public void the_player_with_name_exists(String name) {
        player = playerAccessor.createPlayer(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(player.getName(), name);
    }

    @Given("There is a battleDeck named {string}")
    public void there_is_a_battle_deck_named(String battleDeckName) {
        playeBattleDeckName = battleDeckName;
        battleDeck = deckAccessor.getDeckByName(battleDeckName);
        Assertions.assertNull(battleDeck);
        addInputMocking("random");
        game.battleDeck("battle_deck " + player.getName() + " " + battleDeckName);
        battleDeck = deckAccessor.getDeckByName(battleDeckName);
        Assertions.assertNotNull(battleDeck);
        Assertions.assertEquals(battleDeck.getName(), battleDeckName);
    }


    @Then("The player should have {int} cards in their hand")
    public void the_player_should_have_cards_in_their_hand(Integer numberOfCards) {
        Assertions.assertEquals(numberOfCards, PlayersHand.size());
    }

    @When("The player plays all his cards")
    public void the_player_plays_all_his_cards() {
//        play all cards
        List<Card> playersHandCopy = List.copyOf(PlayersHand);
//        playersHandCopy.get(0).
        for (int i = 0; i < playersHandCopy.size() ; i++) {
            gameBoard.playCard(playersHandCopy.get(i));
            if (playersHandCopy.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("monster")) {
                monsterCount++;
            } else if (playersHandCopy.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("spell")) {
                spellCount++;
            } else if (playersHandCopy.get(i).getCardDescription().split(" ")[0].toLowerCase().equals("trap")) {
                trapCount++;
            }
        }
    }
    @Then("The Card type is placed at its dedicated place on the board")
    public void the_card_type_is_placed_at_its_dedicated_place_on_the_board() {
        List<Monster> monsterSlot = gameBoard.getMonsterSlots();
        List<Spell> spellSlot = gameBoard.getSpellSlots();
        List<Trap> trapSlot = gameBoard.getTrapSlots();

        Assertions.assertEquals(monsterCount, monsterSlot.size());
        Assertions.assertEquals(spellCount, spellSlot.size());
        Assertions.assertEquals(trapCount, trapSlot.size());


    }

    @When("The game starts")
    public void the_game_starts() {
        gameBoard = new GameBoard(battleDeck);
        gameBoard.startGame();
        PlayersHand = gameBoard.getHand();
    }

    @When("The player plays all his monster cards in attack or defence mode")
    public void the_player_plays_all_his_monster_cards_in_attack_or_defence_mode() {
        Random random = new Random();
        CardPosition cardPosition = CardPosition.DEFEND;
        List<Card> battleDeckList = battleDeck.getCards();
        for (int i = 0; i < battleDeckList.size() ; i++) {
            if (battleDeckList.get(i) instanceof Monster monster && random.nextBoolean()) {
                monster.setCardPosition(cardPosition);
            }
        }
        gameBoard = new GameBoard(battleDeck);
//      player plays all his cards
        for (int i = 0; i < battleDeck.getCards().size(); i++) {
            gameBoard.startTurn();
        }
        PlayersHand = gameBoard.getHand();
        List<Card> copyOfPlayersHand = new ArrayList<>(PlayersHand);;
        for (int i = 0; i < copyOfPlayersHand.size(); i++) {
            gameBoard.playCard(copyOfPlayersHand.get(i));
        }
        PlayersHand = copyOfPlayersHand;

    }

    @Then("Its starting life is equal to its attack or defence respectively")
    public void its_starting_life_is_equal_to_its_attack_or_defence_respectively() {
        for (int i = 0; i < PlayersHand.size() ; i++) {
            if (PlayersHand.get(i) instanceof Monster monster) {
                if (monster.getCardPosition() == CardPosition.ATTACK) {
                    Assertions.assertEquals(monster.getLife(),monster.getAttack());
                } else {
                    Assertions.assertEquals(monster.getLife(),monster.getDefence());
                }
            }
        }
    }
}
