Feature: U4 As Alex, I want to create a battle deck so that I can fight opponents.

  Scenario: AC1 - I can create a random battle deck of 20 cards.
    Given The player "Jack" exists
    And There is no deck named "MyFirstBattleDeck"
    When I create a battle deck named "MyFirstBattleDeck"
    Then The battle deck must contain 20 cards exactly

  Scenario: AC2 - A valid battle deck is composed of at least 5 monsters, 3 traps and 3 spells.
    Given The player "Jack" exists
    And There is no deck named "MyFirstBattleDeck"
    When I create a battle deck named "MyFirstBattleDeck"
    Then The battle deck contains at least 5 monsters
    And The battle deck contains at least 3 spells
    And The battle deck contains at least 3 traps

  Scenario Outline: AC3 - I can create a battle deck with custom card type ratios
    Given The player "Jack" exists
    And There is no deck named "MyFirstBattleDeck"
    When I create a battle deck named "MyFirstBattleDeck" with <monsters> monsters, <spells> spells and <traps> traps
    Then The battle deck contains at least <monsters> monsters
    And The battle deck contains at least <spells> spells
    And The battle deck contains at least <traps> traps
    Examples:
      | monsters | spells | traps |
      | 5        |3       |3      |
      | 14       |3       |3      |
      | 5        |3       |12     |
      | 5        |12      |3      |
