Feature: Get Bank Statement

  In order to follow my account balance
  as an account owner
  I want to read my bank statement

  Scenario: Getting a bank statement after performing transactions
    Given Jane has a bank account number 123456
    And she deposits 1000.00 GBP on 01/03/2023
    And she withdraws 100.00 GBP on 14/03/2023
    And she deposits 500.00 GBP on 01/04/2023
    When she gets her bank statement
    Then she should be shown the following:
      | DATE       | AMOUNT  | BALANCE |
      | 01/04/2023 | 500.00  | 1400.00 |
      | 14/03/2023 | -100.00 | 900.00  |
      | 01/03/2023 | 1000.00 | 1000.00 |