Feature: Everything related to users

  Scenario: Getting user by id
    Given I have an invalid token for role "user"
    When I get user by Id 3
    Then I recieve a status code of 403

  Scenario: Getting all the users
    Given I have an invalid token for role "admin"
    When I call get all users endpoint
    Then I recieve a status code of 403