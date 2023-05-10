Feature: U5 As Alex, I want to place my 5 starting cards on the board so that I can start a fight.

  Scenario: AC1 - At the start of the game, I draw the first 5 cards of my battle deck.
    Given The player with name "MrTest" exists
    And There is a battleDeck named "TestBattleDeck"
    When The game starts
    Then The player should have 5 cards in their hand


  Scenario: AC2 - Each card type must be placed at its dedicated place on the board.
    Given The player with name "MrTest" exists
    And There is a battleDeck named "TestBattleDeck"
    When The game starts
    And The player plays all his cards
    Then The Card type is placed at its dedicated place on the board

  Scenario: AC3 - When a monster card is placed in attack or defence mode, then its starting life is equal to its attack or defence respectively.
    Given The player with name "MrTest" exists
    And There is a battleDeck named "TestBattleDeck"
    When The player plays all his monster cards in attack or defence mode
    Then Its starting life is equal to its attack or defence respectively
