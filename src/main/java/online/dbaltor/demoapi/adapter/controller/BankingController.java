package online.dbaltor.demoapi.adapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import online.dbaltor.demoapi.adapter.persistence.AccountDbException;
import online.dbaltor.demoapi.application.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;

import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT;
import static online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest.ACCOUNT_NUMBER_FORMAT_ERROR_MSG;
import static org.springframework.http.MediaType.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Banking", description = "Common banking operations")
@RequestMapping(value = "banking/v1",  produces = APPLICATION_JSON_VALUE)
public class BankingController {

    private @NonNull AccountService accountService;

    @Operation(summary = "Request a deposit transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit transaction successful"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing or malformed body"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PostMapping(value = "deposit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.deposit(transactionRequest.getAccount(), new BigDecimal(transactionRequest.getAmount()));
            return ResponseEntity.ok(TransactionResponse.of("Deposit transaction successful"));
        } catch (AccountDbException e) {
            return handleException(e);
        }
    }
    @Operation(summary = "Request a withdrawal transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal transaction successful"),
            @ApiResponse(responseCode = "400", description = "Bad request - Missing or malformed body"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @PostMapping(value = "withdrawal", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.withdraw(transactionRequest.getAccount(), new BigDecimal(transactionRequest.getAmount()));
            return ResponseEntity.ok(TransactionResponse.of("Withdrawal transaction successful"));
        } catch (AccountDbException e) {
            return handleException(e);
        }
    }

    @Operation(summary = "Request a bank statement ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank statement"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid account number"),
            @ApiResponse(responseCode = "404", description = "Bad request - account not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")})
    @GetMapping("statement/{account}")
    public ResponseEntity<TransactionResponse> getStatement(
            @PathVariable("account")
            @Pattern(regexp = ACCOUNT_NUMBER_FORMAT, message = ACCOUNT_NUMBER_FORMAT_ERROR_MSG)
            String accountNumber) {
        try {
            return ResponseEntity.ok(TransactionResponse.of(accountService.getStatement(accountNumber)));
        } catch (AccountDbException e) {
            return handleException(e);
        }
    }
    private static ResponseEntity<TransactionResponse> handleException(AccountDbException accountDbException) {
        accountDbException.error().ifPresent( error -> log.error("Exception: ", error));
        return switch (accountDbException.errorType()) {
            case ACCOUNT_NOT_FOUND -> ResponseEntity.badRequest().body(TransactionResponse.of("Account not found"));
            case UNEXPECTED -> ResponseEntity.internalServerError().body(TransactionResponse.of("Something went wrong"));
        };
    }
}
