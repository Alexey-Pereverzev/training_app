Feature: Trainee API (controller level)
  Manage trainee accounts and trainings through the public API.

  Background:
    Given the application test context and authentication are prepared

  Scenario: Update trainee successfully
    Given a trainee "Ivan.Petrov" exists
    And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    When I update the trainee "Ivan.Petrov" address to "Almaty"
    Then response status should be 200
    And the trainee address in the response should be "Almaty"

  Scenario: Update trainee fails when trainee not found
    And a trainee "Unknown.User" exists
    And I am authenticated as "Unknown.User" with role "TRAINEE"
    And trainee "Unknown.User" is removed from DB
    When I update the trainee "Unknown.User" address to "Nowhere"
    Then response status should be 401

  Scenario: Delete trainee successfully
    Given a trainee "Nina.Rakhimova" exists
    And the trainee "Nina.Rakhimova" has a training "AnyName" with trainer "Elena.Sokolova" of type "Yoga"
    And I am authenticated as "Nina.Rakhimova" with role "TRAINEE"
    When I delete the trainee "Nina.Rakhimova"
    Then response status should be 200
    And a JMS event should be published

  Scenario: Delete trainee fails when not found
    And a trainee "Not.Exist" exists
    And I am authenticated as "Not.Exist" with role "TRAINEE"
    And trainee "Not.Exist" is removed from DB
    When I delete the trainee "Not.Exist"
    Then response status should be 401

  Scenario: Get trainee trainings with filter
    Given a trainee "Ivan.Petrov" has trainings
    And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    When I request trainings between "2025-01-01" and "2025-01-31" with trainer "Elena.Sokolova"
    Then response status should be 200
    And I should receive a list of trainings matching the filter criteria

  Scenario: Update trainee fails with ownership violation
    Given a trainee "Ivan.Petrov" exists
    And a trainee "Nina.Rakhimova" exists
    And I am authenticated as "Nina.Rakhimova" with role "TRAINEE"
    When I update the trainee "Ivan.Petrov" address to "Almaty"
    Then response status should be 403

  Scenario: Get trainee info fails with wrong role
    Given a trainee "Ivan.Petrov" exists
    And a trainer "Elena.Sokolova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I get trainee "Ivan.Petrov" info
    Then response status should be 403

  Scenario: Delete trainee fails when unauthenticated
    Given a trainee "Ivan.Petrov" exists
    And I am not authenticated
    When I delete the trainee "Ivan.Petrov"
    Then response status should be 401