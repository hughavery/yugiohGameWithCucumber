package uc.seng301.cardbattler.asg3.cards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uc.seng301.cardbattler.asg3.model.Card;

/**
 * Card API fetching functionality, makes use of {@link CardResponse} and
 * Jackson to map the returned JSON to a card
 */
public class CardService implements CardGenerator {
    private static final Logger LOGGER = LogManager.getLogger(CardService.class);
    private static final String CARD_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php?id=";
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private List<Integer> monsterIds;
    private List<Integer> spellIds;
    private List<Integer> trapIds;
    private List<Integer> allIds;
    private Random random;

    /**
     * Create a new card service
     */
    public CardService() {
        getAllIds();
        random = new Random();
    }

    @Override
    public Card getRandomCard() {
        return getRandomCardOfType(CardType.RANDOM);
    }

    @Override
    public Card getRandomCardOfType(CardType cardType) {
        String apiResponse = getResponseFromAPI(cardType);
        if (null != apiResponse && !apiResponse.isEmpty()) {
            CardResponse cardResponse = parseResponse(apiResponse);
            if (null != cardResponse) {
                return cardResponse.toCard();
            }
        }
        // API service unreachable, use a mocked result
        return getOfflineResponse(cardType).toCard();
    }

    /**
     * Gets the response from the API
     * 
     * @return The response body of the request as a String
     */
    public String getResponseFromAPI(CardType cardType) {
        String data = null;
        try {
            int randomCardId;
            switch (cardType) {
                case MONSTER -> randomCardId = monsterIds.get(random.nextInt(monsterIds.size()));
                case SPELL -> randomCardId = spellIds.get(random.nextInt(spellIds.size()));
                case TRAP -> randomCardId = trapIds.get(random.nextInt(trapIds.size()));
                default -> randomCardId = allIds.get(random.nextInt(allIds.size()));
            }
            LOGGER.info("Fetching card with id: {}", randomCardId);
            URL url = new URL(CARD_URL + randomCardId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            LOGGER.info("Api responded with status code: {}", responseCode);

            if (responseCode == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringResult = new StringBuilder();
                while (scanner.hasNext()) {
                    stringResult.append(scanner.nextLine());
                }
                data = stringResult.toString();
                scanner.close();
            } else {
                LOGGER.error("unable to process request to API, response code is '{}'", responseCode);
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing API response", e);
        }
        return data;
    }

    /**
     * Parse the json response to a {@link CardResponse} object using Jackson
     * 
     * @param stringResult String representation of response body (JSON)
     * @return CardResponse decoded from string, null if a JSON decoding error
     *         occurred
     */
    private CardResponse parseResponse(String stringResult) {
        CardResponse cardResponse = null;
        try {
            String cardData = stringResult.substring(9, stringResult.length() - 2);
            cardResponse = objectMapper.readValue(cardData, CardResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing API response", e);
        }
        return cardResponse;
    }

    /**
     * Loads card response from a text file if there is an issue with the API (e.g.
     * there is no internet connection, or the API is down)
     * If there is an error loading cards from this file the application will exit
     * as the state will be unusable
     *
     * @return {@link CardResponse} object with values manually assigned to those
     *         loaded from a file
     */
    private CardResponse getOfflineResponse(CardType cardType) {
        LOGGER.warn("Falling back to offline cards");
        List<CardResponse> cardResponses = null;
        try {
            String fileToLoad = "";
            switch (cardType) {
                case MONSTER -> fileToLoad = "all_monsters.json";
                case SPELL -> fileToLoad = "all_spells.json";
                case TRAP -> fileToLoad = "all_traps.json";
                case RANDOM ->
                    fileToLoad = Arrays.asList("all_monsters.json", "all_spells.json", "all_traps.json")
                            .get(random.nextInt(3));
            }
            cardResponses = objectMapper.readValue(
                    new File(getClass().getClassLoader().getResource(String.valueOf(Paths.get("cards", fileToLoad)))
                            .toURI()),
                    new TypeReference<>() {
                    });
        } catch (URISyntaxException | IOException e) {
            LOGGER.fatal("ERROR parsing offline data, app is now exiting as no further functionality wil work", e);
            System.err.println("Fatal error occurred, no offline data available, make sure you have the conf files.");
            System.exit(1);
        }
        return cardResponses.get(random.nextInt(cardResponses.size()));
    }

    /**
     * updates list of ids for different card types in place.
     * Ids can be used to query the api
     * WARNING: Does not work in jar
     */
    private void getAllIds() {
        monsterIds = new ArrayList<>();
        spellIds = new ArrayList<>();
        trapIds = new ArrayList<>();
        allIds = new ArrayList<>();
        try {
            File monsterIdFile = new File(
                    getClass().getClassLoader().getResource(String.valueOf(Paths.get("cards", "monster_ids.txt")))
                            .toURI());
            try (Scanner myReader = new Scanner(monsterIdFile)) {
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    monsterIds.add(Integer.parseInt(data));
                }
            }
            File spellIdFile = new File(
                    getClass().getClassLoader().getResource(String.valueOf(Paths.get("cards", "spell_ids.txt")))
                            .toURI());
            try (Scanner myReader = new Scanner(spellIdFile)) {
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    spellIds.add(Integer.parseInt(data));
                }
            }
            File trapIdFile = new File(
                    getClass().getClassLoader().getResource(String.valueOf(Paths.get("cards", "trap_ids.txt")))
                            .toURI());
            try (Scanner myReader = new Scanner(trapIdFile)) {
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    trapIds.add(Integer.parseInt(data));
                }
            }
            allIds.addAll(monsterIds);
            allIds.addAll(spellIds);
            allIds.addAll(trapIds);
        } catch (FileNotFoundException | URISyntaxException e) {
            LOGGER.error("Could not load cards id file", e);
        } catch (NullPointerException e) {
            LOGGER.error("Could not load cards id file are you running a test?", e);
        }
    }

}
