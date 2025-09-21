Feature: Trainer API (controller level)
  Manage trainer profiles and trainings through the public API.

  Background:
    Given the application test context and authentication are prepared

  Scenario: Update trainer successfully
    Given a trainer "Dina.Aliyeva" exists
    And I am authenticated as "Dina.Aliyeva" with role "TRAINER"
    When I update the trainer "Dina.Aliyeva" specialization to "Pilates"
    Then response status should be 200
    And the trainer specialization in the response should be "Pilates"

  Scenario: Update trainer fails without specialization
    Given a trainer "Elena.Sokolova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I update the trainer "Elena.Sokolova" without specialization
    Then response status should be 400
    And the response body should contain "Specialization is required."

  Scenario: Update trainer fails with ownership violation
    Given a trainer "Dina.Aliyeva" exists
    And a trainer "Elena.Sokolova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I update the trainer "Dina.Aliyeva" specialization to "Pilates"
    Then response status should be 403

  Scenario: Get trainer profile successfully
    Given a trainer "Elena.Sokolova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I get trainer "Elena.Sokolova" info
    Then response status should be 200

  Scenario: Get trainer profile fails with wrong role
    Given a trainer "Elena.Sokolova" exists
    And I am authenticated as "Ivan.Petrov" with role "TRAINEE"
    When I get trainer "Elena.Sokolova" info
    Then response status should be 403

  Scenario: Get trainer profile fails when not found
    And a trainer "NotExist" exists
    And I am authenticated as "NotExist" with role "TRAINER"
    And trainer "NotExist" is removed from DB
    When I get trainer "NotExist" info
    Then response status should be 401

  Scenario: Get trainer trainings by date
    Given a trainer "Elena.Sokolova" has trainings
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I request trainer trainings between "2025-02-01" and "2025-02-28"
    Then response status should be 200
    And I should receive a list of trainings in that range

  Scenario: Get trainer trainings fails when unauthenticated
    Given a trainer "Elena.Sokolova" has trainings
    And I am not authenticated
    When I request trainer trainings for "Elena.Sokolova" between "2025-02-01" and "2025-02-28"
    Then response status should be 401

  Scenario: Change trainer active status successfully
    Given a trainer "Elena.Sokolova" exists
    And I am authenticated as "Elena.Sokolova" with role "TRAINER"
    When I change trainer "Elena.Sokolova" active status to "true"
    Then response status should be 200
    And the response body should contain "Trainer active status changed to true"

  Scenario: Change trainer active status fails when unauthenticated
    Given a trainer "Elena.Sokolova" exists
    And I am not authenticated
    When I change trainer "Elena.Sokolova" active status to "false"
    Then response status should be 401
