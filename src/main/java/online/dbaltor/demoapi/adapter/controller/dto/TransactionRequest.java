package online.dbaltor.demoapi.adapter.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record TransactionRequest(
        @NotEmpty(message = "Account number is missing")
        @Pattern(regexp = ACCOUNT_NUMBER_FORMAT, message = ACCOUNT_NUMBER_FORMAT_ERROR_MSG)
        String account,
        @NotEmpty(message = "Amount is missing")
        @Pattern(regexp = "^\\d{1,5}(\\.\\d{1,2})?$", message = "Amount can be up to 99999.99 with pence being optional")
        String amount) {
    public static final String ACCOUNT_NUMBER_FORMAT_ERROR_MSG = "Account number must be a 6-digit number";
    public static final String ACCOUNT_NUMBER_FORMAT = "^\\d{6}$";
}
