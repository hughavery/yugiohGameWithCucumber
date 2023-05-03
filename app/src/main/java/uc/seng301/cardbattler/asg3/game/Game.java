package uc.seng301.cardbattler.asg3.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import uc.seng301.cardbattler.asg3.accessor.CardAccessor;
import uc.seng301.cardbattler.asg3.accessor.DeckAccessor;
import uc.seng301.cardbattler.asg3.accessor.PlayerAccessor;
import uc.seng301.cardbattler.asg3.cards.CardGenerator;
import uc.seng301.cardbattler.asg3.cards.CardProxy;
import uc.seng301.cardbattler.asg3.cli.CommandLineInterface;
import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Player;

/**
 * Main game loop functionality for application
 */
public class Game {
    private static final Logger LOGGER = LogManager.getLogger(Game.class);
    private final CommandLineInterface cli;
    private final PlayerAccessor playerAccessor;
    private final DeckAccessor deckAccessor;
    private final CardAccessor cardAccessor;
    private final CardGenerator cardGenerator;
    private final BattleDeckCreator battleDeckCreator;

    private String welcomeMessage = """
            ######################################################
                         Welcome to Yu-Gi-Oh! Clone App
            ######################################################""";

    private String helpMessage = """
            Available Commands:
            "create_player <name>" to create a new player
            "create_deck <player_name> <deck_name>" create a deck with <deck_name> for player <player_name>
            "draw <player_name> <deck_name>" draw a random card to add to deck
            "battle_deck <player_name> <deck_name>" create a battle deck
            "print <player_name>" print player by name
            "exit", "!q" to quit
            "help" print this help text""";

    /**
     * Create a new game with default settings
     */
    public Game() {
        // this will load the config file (hibernate.cfg.xml in resources folder)
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        playerAccessor = new PlayerAccessor(sessionFactory);
        deckAccessor = new DeckAccessor(sessionFactory);
        cardAccessor = new CardAccessor(sessionFactory);
        cardGenerator = new CardProxy();
        battleDeckCreator = new BattleDeckCreator(cardGenerator);
        cli = new CommandLineInterface(System.in, System.out);
    }

    /**
     * Create a new game with custom card generation, command line interface, and
     * existing session factory
     *
     * @param customCardGenerator  Custom card generator implementation to get
     *                             around calling the API
     * @param commandLineInterface Custom command line interface to get input from
     *                             other sources
     * @param sessionFactory       Existing session factory to use for accessing H2
     */
    public Game(CardGenerator customCardGenerator, CommandLineInterface commandLineInterface,
            SessionFactory sessionFactory) {
        playerAccessor = new PlayerAccessor(sessionFactory);
        deckAccessor = new DeckAccessor(sessionFactory);
        cardAccessor = new CardAccessor(sessionFactory);
        cardGenerator = customCardGenerator;
        battleDeckCreator = new BattleDeckCreator(cardGenerator);
        cli = commandLineInterface;
    }

    /**
     * Main application/game loop
     */
    public void play() {
        cli.printLine(welcomeMessage);
        cli.printLine(helpMessage);
        boolean exit = false;
        while (!exit) {
            String input = cli.getNextLine();
            LOGGER.info("User input: {}", input);
            switch (input.split(" ")[0]) {
                case "create_player" -> createPlayer(input);
                case "create_deck" -> createDeck(input);
                case "draw" -> draw(input);
                case "battle_deck" -> battleDeck(input);
                case "print" -> print(input);
                case "exit", "!q" -> exit = true;
                case "help" -> cli.printLine(helpMessage);
                default -> {
                    cli.printLine("Invalid command, use \"help\" for more info");
                    LOGGER.info("User entered invalid input, {}", input);
                }
            }
        }
        LOGGER.info("User quitting application.");
        cli.printLine("Bye!");
    }

    /**
     * Functionality for the create_player command
     *
     * @param input user input to the command
     */
    private void createPlayer(String input) {
        String[] uInputs = input.split(" ");
        Player player = null;
        if (uInputs.length != 2) {
            cli.printLine("Command incorrect use \"help\" for more information");
            return;
        }
        try {
            player = playerAccessor.createPlayer(uInputs[1]);
        } catch (IllegalArgumentException e) {
            cli.printLine(String.format("Could not create Player. %s: %s", e.getMessage(), uInputs[1]));
            return;
        }
        playerAccessor.persistPlayer(player);
        LOGGER.info("Valid input, created user {}: {}", player.getPlayerId(), player.getName());
        cli.printLine(String.format("Created player %d: %s", player.getPlayerId(), player.getName()));
    }

    /**
     * Functionality for the create_deck command
     *
     * @param input user input to the command
     */
    private void createDeck(String input) {
        String[] uInputs = input.split(" ");
        Deck deck = null;
        if (uInputs.length != 3) {
            cli.printLine("Command incorrect use \"help\" for more information");
            return;
        }
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (null == player) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        try {
            deck = deckAccessor.createDeck(uInputs[2], player, new ArrayList<>());
        } catch (IllegalArgumentException e) {
            cli.printLine(String.format("Could not create deck. %s: %s", e.getMessage(), uInputs[2]));
            return;
        }
        deckAccessor.persistDeck(deck);
        LOGGER.info("Valid input, created deck {} for user {} with name {}", deck.getDeckId(), player.getPlayerId(),
                deck.getName());
        cli.printLine(String.format("Created deck %d: %s for %s", deck.getDeckId(), deck.getName(), player.getName()));
    }

    /**
     * Functionality for the draw command
     *
     * @param input user input to the command
     */
    public void draw(String input) {
        String[] uInputs = input.split(" ");
        if (uInputs.length != 3) {
            cli.printLine("Command incorrect use \"help\" for more information");
            return;
        }
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (null == player) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        Deck deck = deckAccessor.getDeckByPlayerNameAndDeckName(uInputs[1], uInputs[2]);
        if (null == deck) {
            cli.printLine(String.format("No deck: %s, for player %s", uInputs[2], uInputs[1]));
            return;
        }
        Card card = cardGenerator.getRandomCard();
        cli.printLine("You drew...");
        cli.printLine(card.getCardDescription());
        cli.printLine("Do you want to add this card to your deck? Y/N");
        String choice;
        boolean gettingInput = true;
        while (gettingInput) {
            choice = cli.getNextLine();
            switch (choice.split(" ")[0]) {
                case "Y", "y", "Yes", "yes", "YES" -> {
                    deck.addCards(card);
                    cardAccessor.persistCard(card);
                    deckAccessor.updateDeck(deck);
                    cli.printLine("Card saved");
                    gettingInput = false;
                }
                case "N", "n", "No", "no", "NO" -> {
                    cli.printLine("Card not saved");
                    gettingInput = false;
                }
                default -> cli.printLine("Invalid option please input Yes or No");

            }
        }
    }

    /**
     * Functionality to create a new battle deck
     * 
     * @param input user input to the command
     */
    public void battleDeck(String input) {
        String[] uInputs = input.split(" ");
        if (uInputs.length != 3) {
            cli.printLine("Command incorrect use \"help\" for more information");
            return;
        }
        Deck deck;
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (player == null) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        if (deckAccessor.getDeckByName(uInputs[2]) != null) {
            cli.printLine(String.format("A deck named: %s already exists", uInputs[2]));
            return;
        }
        cli.printLine(
                "Do you want a random distribution (type 'random') or choose the number of each card (type 'choice')");
        String choice;
        boolean gettingInput = true;
        Map<String, Integer> cardFrequencyValues = new HashMap<>();
        while (gettingInput) {
            choice = cli.getNextLine();
            switch (choice.split(" ")[0].toLowerCase()) {
                case "random", "r" -> {
                    cardFrequencyValues = getCardFrequencyValues();
                    gettingInput = false;
                }
                case "choice", "c" -> {
                    cardFrequencyValues = getCustomCardFrequencyValues();
                    gettingInput = false;
                }
                default -> cli.printLine("Invalid option please input Random or Choice");
            }
        }
        try {
            deck = deckAccessor.createDeck(uInputs[2], player, new ArrayList<>());
        } catch (IllegalArgumentException e) {
            cli.printLine(String.format("Could not create deck. %s: %s", e.getMessage(), uInputs[2]));
            return;
        }
        cli.printLine("Accessing rate-limited API, operation may take a few seconds...");
        battleDeckCreator.populateRandomBattleDeck(deck, cardFrequencyValues.get("Monster"),
                cardFrequencyValues.get("Spell"), cardFrequencyValues.get("Trap"));
        deck.getCards().forEach(cardAccessor::persistCard);
        deckAccessor.persistDeck(deck);
        cli.printLine("Deck created: " + deck.toString());
    }

    /**
     * Get custom frequencies for card types
     * 
     * @return a map of cards types with the number of each of these cards to
     *         compose a deck
     */
    private Map<String, Integer> getCustomCardFrequencyValues() {
        Map<String, Integer> cardFrequencyValues = new HashMap<>();
        Map<String, Integer> options = getCardFrequencyValues();
        Iterator<Entry<String, Integer>> i = options.entrySet().iterator();
        cli.printLine(String.format("""
                Please enter the starting number of each card type you want (Monsters, Spells, Traps).
                The total must not exceed %d. If too few cards given, random cards will be added to make it to %d.""",
                BattleDeckCreator.DECK_SIZE, BattleDeckCreator.DECK_SIZE));
        while (i.hasNext()) {
            String choice;
            boolean gettingInput = true;
            Entry<String, Integer> cardEntry = i.next();
            while (gettingInput) {
                cli.printLine(String.format("How many %s cards do you want, minimum: %d", cardEntry.getKey(),
                        cardEntry.getValue()));
                choice = cli.getNextLine();
                try {
                    int numSelected = Integer.parseInt(choice);
                    if (numSelected < cardEntry.getValue()) {
                        cli.printLine(String.format("Cannot have less than %d %s cards", cardEntry.getValue(),
                                cardEntry.getKey()));
                    } else {
                        cardFrequencyValues.put(cardEntry.getKey(), numSelected);
                        gettingInput = false;
                    }
                } catch (NumberFormatException nfe) {
                    cli.printLine("Number expected");
                }
            }

            if (!i.hasNext() && cardFrequencyValues.values().stream().mapToInt(Integer::intValue)
                    .sum() > BattleDeckCreator.DECK_SIZE) {
                cli.printLine(String.format("Total must not exceed %d", BattleDeckCreator.DECK_SIZE));
                // reset card frequency values and iterator to ask for card numbers again
                i = options.entrySet().iterator();
            }
        }
        return cardFrequencyValues;
    }

    /**
     * Functionality for the print command
     *
     * @param input user input to the command
     */
    private void print(String input) {
        String[] uInputs = input.split(" ");
        if (uInputs.length != 2) {
            cli.printLine("Command incorrect use \"help\" for more information");
            return;
        }
        Player player = playerAccessor.getPlayerByName(uInputs[1]);
        if (player == null) {
            cli.printLine(String.format("No player named: %s", uInputs[1]));
            return;
        }
        cli.printLine(player.toString());
    }

    /**
     * Get the default minimum frequency values to build a valid battle deck
     * 
     * @return a map of card types with their minimum number of cards for that type
     */
    private Map<String, Integer> getCardFrequencyValues() {
        // note that keys are sorted in a specific order
        Map<String, Integer> cardFrequencyValues = new LinkedHashMap<>();
        cardFrequencyValues.put("Monster", BattleDeckCreator.MIN_NUM_MONSTERS);
        cardFrequencyValues.put("Spell", BattleDeckCreator.MIN_NUM_SPELLS);
        cardFrequencyValues.put("Trap", BattleDeckCreator.MIN_NUM_TRAPS);
        return cardFrequencyValues;
    }
}
