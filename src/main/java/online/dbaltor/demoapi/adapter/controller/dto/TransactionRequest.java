package online.dbaltor.demoapi.adapter.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransactionRequest {
    public static final String ACCOUNT_NUMBER_FORMAT_ERROR_MSG = "Account number must be a 6-digit number";
    public static final String ACCOUNT_NUMBER_FORMAT = "^\\d{6}$";

    @NotEmpty(message = "Account number is missing")
    @Pattern(regexp = ACCOUNT_NUMBER_FORMAT, message = ACCOUNT_NUMBER_FORMAT_ERROR_MSG)
    private String account;

    @NotEmpty(message = "Amount is missing")
    @Pattern(regexp = "^\\d{1,5}(\\.\\d{1,2})?$", message = "Amount can be up to 99999.99 with pence being optional")
    private String amount;
}
