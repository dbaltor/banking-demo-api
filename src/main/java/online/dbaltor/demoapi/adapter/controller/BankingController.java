package online.dbaltor.demoapi.adapter.controller;

import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT;
import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT_ERROR_MSG;
import static org.springframework.http.MediaType.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.application.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Banking", description = "Common banking operations")
@RequestMapping(value = "/banking/v1", produces = APPLICATION_JSON_VALUE)
public class BankingController {
    private @NonNull AccountService accountService;

    @Operation(summary = "Request a deposit transaction")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Deposit transaction successful"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request - Missing or malformed body"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @PostMapping(value = "deposit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.deposit(
                    transactionRequest.account(), new BigDecimal(transactionRequest.amount()));
            return ResponseEntity.ok(new TransactionResponse("Deposit transaction successful"));
        } catch (AccountException e) {
            return handleException(e);
        }
    }

    @Operation(summary = "Request a withdrawal transaction")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Withdrawal transaction successful"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request - Missing or malformed body"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @PostMapping(value = "withdrawal", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.withdraw(
                    transactionRequest.account(), new BigDecimal(transactionRequest.amount()));
            return ResponseEntity.ok(new TransactionResponse("Withdrawal transaction successful"));
        } catch (AccountException e) {
            return handleException(e);
        }
    }

    @Operation(summary = "Request a bank statement ")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Bank statement"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request - invalid account number"),
                @ApiResponse(responseCode = "404", description = "Bad request - account not found"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
    @GetMapping(value = {"statement", "statement/", "statement/{account}"})
    public ResponseEntity<TransactionResponse> getStatement(
            @PathVariable("account")
                    @Pattern(
                            regexp = ACCOUNT_NUMBER_FORMAT,
                            message = ACCOUNT_NUMBER_FORMAT_ERROR_MSG)
                    String accountNumber) {
        try {
            return ResponseEntity.ok(
                    new TransactionResponse(accountService.getStatement(accountNumber)));
        } catch (AccountException e) {
            return handleException(e);
        }
    }

    private static ResponseEntity<TransactionResponse> handleException(
            AccountException accountException) {
        accountException.error().ifPresent(error -> log.error("Exception: ", error));
        return switch (accountException.errorType()) {
            case ACCOUNT_NOT_FOUND ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new TransactionResponse("Account not found"));
            case INSUFFICIENT_FUNDS ->
                    ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new TransactionResponse("Insufficient funds"));
            case UNEXPECTED ->
                    ResponseEntity.internalServerError()
                            .body(new TransactionResponse("Something went wrong"));
        };
    }
}
