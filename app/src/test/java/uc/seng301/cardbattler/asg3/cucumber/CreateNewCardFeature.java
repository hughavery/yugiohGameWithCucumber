package uc.seng301.cardbattler.asg3.cucumber;

import java.util.logging.Level;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uc.seng301.cardbattler.asg3.accessor.CardAccessor;
import uc.seng301.cardbattler.asg3.model.Card;
import uc.seng301.cardbattler.asg3.model.CardPosition;
import uc.seng301.cardbattler.asg3.model.Monster;

public class CreateNewCardFeature {
    private CardAccessor cardAccessor;
    private Card card;

    private String cardName;
    private Integer cardAttack;
    private Integer cardDefence;
    private Integer cardLife;
    private Exception expectedException;

    @Before
    public void setup() {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Configuration configuration = new Configuration();
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        cardAccessor = new CardAccessor(sessionFactory);
    }

    @Given("There is no card with name {string}")
    public void there_is_no_card_with_name(String cardName) {
        Assertions.assertNull(cardAccessor.getCardByName(cardName));
    }

    @When("I create a card named {string} with attack: {int}, defence: {int}, life: {int}")
    public void i_create_a_card_named_with_attack_defence_life(String cardName, Integer cardAttack, Integer cardDefence,
            Integer cardLife) {
        this.cardName = cardName;
        this.cardAttack = cardAttack;
        this.cardDefence = cardDefence;
        this.cardLife = cardLife;
        Assertions.assertNotNull(cardName);
        Assertions.assertTrue(cardAttack > 0);
        Assertions.assertTrue(cardDefence > 0);
        Assertions.assertTrue(cardLife >= 0);
    }

    @Then("The card is created with the correct name, attack, defence, and life")
    public void the_card_is_created_with_the_correct_name_attack_defence_and_life() {
        card = cardAccessor.createMonster(cardName, cardAttack, cardDefence, cardLife, CardPosition.ATTACK);
        Assertions.assertNotNull(card);
        Monster monsterCard = (Monster) card;
        Assertions.assertEquals(cardName, card.getName());
        Assertions.assertEquals(cardAttack, monsterCard.getAttack());
        Assertions.assertEquals(cardDefence, monsterCard.getDefence());
        Assertions.assertEquals(cardLife, monsterCard.getLife());
    }

    @When("I create an invalid card named {string} with attack: {int}, defence: {int}, life: {int}")
    public void i_create_an_invalid_card_named_with_attack_defence_life(String cardName, Integer cardAttack,
            Integer cardDefence,
            Integer cardLife) {
        expectedException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> cardAccessor.createMonster(cardName, cardAttack, cardDefence, cardLife, CardPosition.ATTACK));
    }

    @Then("An exception is thrown")
    public void an_exception_is_thrown() {
        Assertions.assertNotNull(expectedException);
    }

}
