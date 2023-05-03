Feature: U2 - As a player I want to be able to create a deck so that I can keep track of my cards and use them to battle

  Scenario: AC1 - I can create a deck with a non-empty alphanumeric name and at least 1 card
    Given A player named "Jane" exists
    And I have a card "Minotaur" with attack 60, defence 50, and life 110
    And The player has no decks with the name "My First Deck"
    When I create a deck "My First Deck" with a card
    Then The deck is created for the player with the correct name and card

  Scenario: AC2 - I can not create a deck if I already have a deck with the same name
    Given A player named "Jane" exists
    And I have a card "Minotaur" with attack 60, defence 50, and life 110
    And The player has a deck with the name "My First Deck" and a card
    When I create an invalid deck "My First Deck" with a card
    Then I am not allowed to create the deck

  Scenario: AC3 - I can create a deck if it has no cards
    Given A player named "Jane" exists
    When I create a deck "My Empty Deck" without a card
    Then The deck is created for the player with the correct name and no cards
