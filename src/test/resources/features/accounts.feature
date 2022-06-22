Feature: Everything related to accounts

  Scenario: Getting accounts of a user is Status OK
    Given I have an valid token for role "admin" to access accounts
    When I call get accounts by IBAN "NL21INHO0123400081"
    Then I receive a status code of 200

  Scenario: Getting all transactions by their IBAN number is Status Forbidden
    Given I have an invalid token for role "admin" to access transactions
    When I call get transactions by IBAN "NL51INHO0123400029" with startDate "2022-04-03T10:25:57" and endDate "2022-05-27T16:27:39" and skip 0 and limit 4
    Then I receive a status code of 403

  Scenario: Getting all transaction by their IBAN is status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029" with startDate "2022-04-03T10:25:57" and endDate "2022-05-27T16:27:39" and skip 0 and limit 4
    Then I receive a status code of 200

  Scenario: Getting all transactions of other user with user rights is status Unauthorized
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400455" with startDate "2022-04-03T10:25:57" and endDate "2022-05-27T16:27:39" and skip 0 and limit 4
    Then I receive a status code of 401

  Scenario: Getting all transactions of the user by the amount is Status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029" by amount
    Then I receive a status code of 200

  Scenario: Getting all transactions by amount is status OK
    Given I have an valid token for role "user" to access account transactions
    When I call get transactions by IBAN "NL51INHO0123400029" by operator "="
    Then I receive a status code of 200


  Scenario: Create account is status OK
    Given I have an valid token for role "admin" to create account
    And I gave valid 10 and account type "current"
    When I call post account
    Then I receive a status code of 200

  Scenario: Create account when a user have already a current account status conflict
    Given I have an valid token for role "admin" to create account
    And I gave valid 5 and account type "current"
    When I call post account
    Then I receive a status code of 409

  Scenario: Create account for a user role user status unauthorized
    Given I have an valid token for role "user" to create account
    And I gave valid 12 and account type "current"
    When I call post account
    Then I receive a status code of 403


