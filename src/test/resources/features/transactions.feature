Feature: Everything related to transactions

  Scenario: Getting all transactions is status OK
    Given I have an valid token for role "admin" to access all transactions of users
    When I call the get all transactions endpoint
    Then I receive a status code of 200 for listing all transactions


  Scenario: Creating a transactions is status OK
    Given I have a valid token for role "user" to create transaction
    And I have a valid transaction object with amount "4.00" and fromAccount "NL51INHO0123400029" and toAccount "NL51INHO0123400455" and TransactionType "transfer"
    When I call the post transaction endpoint
    Then I receive a status code of 200 for creating a transaction

  Scenario: Getting all transactions with invalid token is status Forbidden
    Given I have an invalid token for role "admin" to access all transactions of users
    When I call the get all transactions endpoint
    Then I receive a status code of 403 for listing all transactions

