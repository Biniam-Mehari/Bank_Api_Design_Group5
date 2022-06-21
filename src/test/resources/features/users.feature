Feature: Everything related to users

  Scenario: Getting user by id is Status Forbidden
    Given I have an invalid token for role "user"
    Given When I get user by Id 3
    Then I recieve a status code of 403

  Scenario: Getting all users with invalid token is Status Forbidden
    Given I have an invalid token for role "admin"
    When I call get all users endpoint
    Then I recieve a status code of 403

  Scenario: Getting total balance of user
    Given I have an valid token for role "admin"
    When I call get total balance of user by Id 5
    Then I recieve a status code of 200


  Scenario: Getting total balance of user is status Forbidden
    Given I have an invalid token for role "admin"
    When I call get total balance of user by Id 3
    Then I recieve a status code of 403

  Scenario: Getting all users is status OK
    Given I have an valid token for role "admin"
    When I call correctly get all users endpoint
    Then I recieve a status code of 200

  Scenario: Posting a new user
    When I make a post request to the users endpoint
    Then I receive a status code of 200


       



    