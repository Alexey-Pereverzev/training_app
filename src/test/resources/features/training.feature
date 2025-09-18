Feature: Training API (controller level)
  Manage trainings through the public API.

  Background:
    Given the application test context and authentication are prepared

  Scenario: Create training successfully
    Given a trainer "Elena.Sokolova" exists
    And a trainee "Anna.Ivanova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I create training "Power Yoga" for trainee "Anna.Ivanova" with duration 60 minutes
    Then response status should be 201
    And the response body should contain "created successfully"
    And a JMS event should be published

  Scenario: Create training fails when trainer not found
    Given a trainee "Anna.Ivanova" exists
    And a trainer "Unknown" exists
    And I am authenticated as "Unknown" with role "TRAINER"
    And trainer "Unknown" is removed from DB
    When I create training "Boxing" for trainee "Anna.Ivanova" with trainer "Unknown"
    Then response status should be 401

  Scenario: Create training fails with ownership violation
    Given a trainer "Elena.Sokolova" exists
    And a trainee "Anna.Ivanova" exists
    And a trainer "Other.Coach" exists
    And I am authenticated as "Other.Coach" with role "TRAINER"
    When I create training "Morning Yoga" for trainee "Anna.Ivanova" with trainer "Elena.Sokolova"
    Then response status should be 403

  Scenario: Delete training successfully
    Given a training "Morning Yoga" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I delete training "Morning Yoga"
    Then response status should be 200
    And the response body should contain "deleted successfully"
    And a JMS event should be published

  Scenario: Delete training fails when not found
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I delete training "Non.Existing"
    Then response status should be 404
    And the response body should contain "Not Found"

  Scenario: Delete training fails for past training
    Given a past training named "Old Yoga" exists in DB
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I delete training "Old Yoga"
    Then response status should be 409
    And the response body should contain "Deleting past trainings prohibited"

  Scenario: Delete training fails when unauthenticated
    Given a training "Morning Yoga" exists
    And I am not authenticated
    When I delete training "Morning Yoga"
    Then response status should be 401

  Scenario: Trigger hours sync successfully
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I trigger training hours sync
    Then response status should be 200
    And the response body should contain "txId"

  Scenario: Trigger hours sync fails with wrong role
    And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    When I trigger training hours sync
    Then response status should be 403

