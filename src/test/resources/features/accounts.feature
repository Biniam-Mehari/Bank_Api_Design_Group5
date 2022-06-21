Feature: Everything related to accounts

  Scenario: Getting accounts of a user is Status OK
    Given I have an valid token for role "admin" to access accounts
    When I call get accounts by IBAN "NL21INHO0123400081"
    Then I receive a status code of 200

  Scenario: Getting all transactions by their IBAN number is Status Forbidden
    Given I have an invalid token for role "admin" to access transactions
    When I call get transactions by IBAN "NL21INHO0123400789"
    Then I receive a status code of 403

  Scenario: Getting all transaction by their IBAN is status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029"
    Then I receive a status code of 200

  Scenario: Getting all transactions of other user with user rights is status Unauthorized
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400455"
    Then I receive a status code of 401

  Scenario: Getting all transactions of the user by the amount is Status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029" by amount
    Then I receive a status code of 200

  Scenario: Getting all transactions by amount is status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029" by operator "="
    Then I receive a status code of 200


