Feature: Everything related to accounts

  Scenario: Getting accounts of a user
    Given I have an valid token for role "admin" to access accounts
    When I call get accounts by IBAN "NL21INHO0123400081"
    Then I receive a status code of 200

  Scenario: Getting all transactions of a user by their IBAN number
    Given I have an invalid token for role "admin" to access transactions
    When I call get transactions by IBAN "NL21INHO0123400789"
    Then I receive a status code of 403
