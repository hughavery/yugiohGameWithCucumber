Feature: U3 As Alex, I want to draw random cards from an external API so that I can build a deck from them
  Scenario: AC1 - I can draw a random card that has valid name, attack, and defense
    Given I have a player "Jane"
    And I have an empty deck "MyFirstDeck"
    When I draw a randomly selected card
    Then I receive a random monster card with valid stats

  Scenario: AC2 - If I find a suitable card, I can add the card to my deck
    Given I have a player "Jane"
    And I have an empty deck "MyFirstDeck"
    And While playing I draw a randomly selected card
    When I decide I want to add the card to my deck
    Then The card is added to my deck

  Scenario: AC3 - I can decide to ignore a card and not add it to my deck
    Given I have a player "Jane"
    And I have an empty deck "MyFirstDeck"
    And While playing I draw a randomly selected card
    When I decide I want to ignore the card
    Then The card is not added to my deck