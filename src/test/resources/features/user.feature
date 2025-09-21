Feature: Authentication & Users API (controller level)
  Manage authentication, authorization, and user lifecycle via the public API.

  Background:
    Given the application test context and authentication are prepared

  Scenario: Authenticate successfully with valid credentials
    Given a user "trainee1" with password "pw123" exists
    When I authorize with username "trainee1" and password "pw123"
    Then response status should be 200
    And I should receive a valid JWT token

  Scenario: Reject login with invalid password
    Given a user "trainee1" with password "pw123" exists
    When I authorize with username "trainee1" and password "wrong"
    Then response status should be 401

  Scenario: Return role for valid user
    Given a user "trainer1" with role "ROLE_TRAINER" exists
    When I request role for username "trainer1" and password "pw123"
    Then response status should be 200
    And I should get role "ROLE_TRAINER"

  Scenario: Reject login with missing username
    When I authorize with username "" and password "pw123"
    Then response status should be 400

  Scenario: Register trainee successfully
    When I register trainee with first name "Ivan" and last name "Petrov"
    Then response status should be 201
    And the response body should contain "Ivan.Petrov"

  Scenario: Register trainer successfully
    When I register trainer with first name "Dina" and last name "Aliyeva" specialization "Yoga"
    Then response status should be 201
    And the response body should contain "Dina.Aliyeva"

  Scenario: Change password successfully
    Given a user "trainee1" with password "old" exists
    And I am authenticated as "trainee1" with role "TRAINEE"
    When I change password for "trainee1" from "old" to "new"
    Then response status should be 200
    And the response body should contain "Password successfully changed"

  Scenario: Reject password change with wrong old password
    Given a user "trainee1" with password "old" exists
    And I am authenticated as "trainee1" with role "TRAINEE"
    When I change password for "trainee1" from "wrong" to "new"
    Then response status should be 400
    And the response body should contain "Invalid old password"

  Scenario: Logout successfully
    And I am authenticated as "trainee1" with role "TRAINEE"
    When I logout
    Then response status should be 200
    And the response body should contain "User logged out successfully"

  Scenario: Reject logout when unauthenticated
    And I am not authenticated
    When I logout
    Then response status should be 401