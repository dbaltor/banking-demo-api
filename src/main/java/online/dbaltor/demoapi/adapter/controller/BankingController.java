package online.dbaltor.demoapi.adapter.controller;

import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT;
import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT_ERROR_MSG;
import static online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse.*;
import static org.springframework.http.MediaType.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Banking", description = "Common banking operations")
public interface BankingController {

    String BAD_REQUEST = "Bad request";
    String INVALID_ACCOUNT_NUMBER_ERROR_MESSAGE = "The account number informed is invalid";
    String NOT_FOUND = "Not found";
    String ACCOUNT_NOT_FOUND_ERROR_MESSAGE = "There is no account with the number informed.";
    String INTERNAL_SERVER_ERROR = "Internal server error";
    String UNEXPECTED_ERROR_MESSAGE = "There has been an unexpected error. Please contact our support.";

    @Operation(
            summary = "Request a deposit transaction",
            description = "This operation deposits the amount informed into the account indicated.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The deposit transaction was successful",
                    content = {@Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    name = "Status 200 will be returned if the deposit transaction was successful.",
                                    summary = "Deposit executed",
                                    value = "{\"message\": \"" + DEPOSIT_TRANSACTION_SUCCESSFUL + "\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = BAD_REQUEST,
                    content = {@Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    name = INVALID_ACCOUNT_NUMBER_ERROR_MESSAGE,
                                    summary = BAD_REQUEST,
                                    value = "{\"message\": \"" + ACCOUNT_NUMBER_FORMAT_ERROR_MSG + "\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = NOT_FOUND,
                    content = {@Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    name = ACCOUNT_NOT_FOUND_ERROR_MESSAGE,
                                    summary = NOT_FOUND,
                                    value = "{\"message\": \"" + ACCOUNT_NOT_FOUND + "\"}"
                            )
                    )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = INTERNAL_SERVER_ERROR,
                    content = {@Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionResponse.class),
                            examples = @ExampleObject(
                                    name = UNEXPECTED_ERROR_MESSAGE,
                                    summary = INTERNAL_SERVER_ERROR,
                                    value = "{\"message\": \"" + SOMETHING_WENT_WRONG + "\"}"
                            )
                    )}
            )
    })
    ResponseEntity<TransactionResponse> deposit(
            @RequestBody(
                    description = "Transaction information",
                    required = true,
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionRequest.class),
                            examples = @ExampleObject(
                                    name = "Deposit 100 EUR to account 12345",
                                    summary = "Deposit 100 EUR",
                                    value = "{\"account\": 123456, \"amount\": 100}"
                            ))
            )
            @Valid TransactionRequest transactionRequest);

    @Operation(
            summary = "Request a withdrawal transaction",
            description = "This operation withdraws the amount informed from the account indicated.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The withdrawal transaction was successful",
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = "Status 200 will be returned if the withdrawal transaction was successful.",
                                            summary = "Withdrawal executed",
                                            value = "{\"message\": \"" + WITHDRAWAL_TRANSACTION_SUCCESSFUL + "\"}"
                                    )
                            )}
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = BAD_REQUEST,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = INVALID_ACCOUNT_NUMBER_ERROR_MESSAGE,
                                            summary = BAD_REQUEST,
                                            value = "{\"message\": \"" + ACCOUNT_NUMBER_FORMAT_ERROR_MSG + "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = NOT_FOUND,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = ACCOUNT_NOT_FOUND_ERROR_MESSAGE,
                                            summary = NOT_FOUND,
                                            value = "{\"message\": \"" + ACCOUNT_NOT_FOUND + "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = "The account balance is not sufficient for this operation.",
                                            summary = "Conflict",
                                            value = "{\"message\": \"" + INSUFFICIENT_FUNDS + "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = INTERNAL_SERVER_ERROR,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = UNEXPECTED_ERROR_MESSAGE,
                                            summary = INTERNAL_SERVER_ERROR,
                                            value = "{\"message\": \"" + SOMETHING_WENT_WRONG + "\"}"
                                    )
                            )}
                    )
            })
    ResponseEntity<TransactionResponse> withdraw(
            @RequestBody(
                    description = "Transaction information",
                    required = true,
                    content = @Content(
                            mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionRequest.class),
                            examples = @ExampleObject(
                                    name = "Withdraw 100 EUR from account 12345",
                                    summary = "Withdraw 100 EUR",
                                    value = "{\"account\": 123456, \"amount\": 100}"
                            ))
            )
            @Valid TransactionRequest transactionRequest);

    @Operation(
            summary = "Request a bank statement ",
            description = "This operation generates a bank statement.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Bank statement",
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = "Banking statement with the transactions in reverse chronological order.",
                                            summary = "Bank statement",
                                            value = "{\"message\": \"DATE       | AMOUNT  | BALANCE\\n" +
                                                    "01/04/2023 | 500.00  | 1400.00\\n" +
                                                    "14/03/2023 | -100.00 | 900.00\\n" +
                                                    "01/03/2023 | 1000.00 | 1000.00" +
                                                    "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = BAD_REQUEST,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = INVALID_ACCOUNT_NUMBER_ERROR_MESSAGE,
                                            summary = BAD_REQUEST,
                                            value = "{\"message\": \"" + ACCOUNT_NUMBER_FORMAT_ERROR_MSG + "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = NOT_FOUND,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = ACCOUNT_NOT_FOUND_ERROR_MESSAGE,
                                            summary = NOT_FOUND,
                                            value = "{\"message\": \"" + ACCOUNT_NOT_FOUND + "\"}"
                                    )
                            )}
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = INTERNAL_SERVER_ERROR,
                            content = {@Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = TransactionResponse.class),
                                    examples = @ExampleObject(
                                            name = UNEXPECTED_ERROR_MESSAGE,
                                            summary = INTERNAL_SERVER_ERROR,
                                            value = "{\"message\": \"" + SOMETHING_WENT_WRONG + "\"}"
                                    )
                            )}
                    )
            })
    ResponseEntity<TransactionResponse> getStatement(
            @Parameter(description = "Account number", required = true, example = "123456")
            @Pattern(
                    regexp = ACCOUNT_NUMBER_FORMAT,
                    message = ACCOUNT_NUMBER_FORMAT_ERROR_MSG)
            String accountNumber);
}
