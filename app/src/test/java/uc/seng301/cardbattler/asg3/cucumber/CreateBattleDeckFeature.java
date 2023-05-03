package uc.seng301.cardbattler.asg3.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import uc.seng301.cardbattler.asg3.accessor.DeckAccessor;
import uc.seng301.cardbattler.asg3.accessor.PlayerAccessor;
import uc.seng301.cardbattler.asg3.model.Deck;
import uc.seng301.cardbattler.asg3.model.Player;

public class CreateBattleDeckFeature {
    private Player player;
    private PlayerAccessor playerAccessor;
    private Deck deck;
    private DeckAccessor deckAccessor;

    @Given("The player {string} exists")
    public void the_player_exists(String name) {
        player = playerAccessor.createPlayer(name);
        Long playerId = playerAccessor.persistPlayer(player);
        Assertions.assertNotNull(player);
        Assertions.assertNotNull(playerId);
        Assertions.assertSame(name, player.getName());
    }

    @Given("There is no deck named {string}")
    public void there_is_no_deck_named(String name) {
        deck = deckAccessor.getDeckByName(name);
        Assertions.assertNull(deck);
    }

    @When("I create a battle deck named {string}")
    public void i_create_a_battle_deck_named(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The battle deck must contain {int} cards exactly")
    public void the_battle_deck_must_contain_cards_exactly(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
