Feature: Training Type API (controller level)
  Manage training types through the public API.

  Background:
    Given the application test context and authentication are prepared

  Scenario: Get training types successfully
    Given training types "Yoga" and "Pilates" exist
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I request training types
    Then response status should be 200
    And the response body should contain "Yoga"
    And the response body should contain "Pilates"

  Scenario: Get training types fails with wrong role
    Given training types "Yoga" and "Pilates" exist
    And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    When I request training types
    Then response status should be 403

  Scenario: Get training types fails when unauthenticated
    Given training types "Yoga" and "Pilates" exist
    And I am not authenticated
    When I request training types
    Then response status should be 401
