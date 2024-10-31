package online.dbaltor.demoapi.adapter.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record TransactionResponse(@NotEmpty String message) {
    public static final String ACCOUNT_NOT_FOUND = "Account not found";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong";
    public static final String DEPOSIT_TRANSACTION_SUCCESSFUL = "Deposit transaction successful";
    public static final String WITHDRAWAL_TRANSACTION_SUCCESSFUL = "Withdrawal transaction successful";
}