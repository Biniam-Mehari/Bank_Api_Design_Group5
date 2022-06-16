Feature: Everything related to users

  Scenario: Retrieve all users is status OK
    When I receive all users
    Then I get http status 200
