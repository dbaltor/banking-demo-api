package online.dbaltor.demoapi.adapter.controller;

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

import java.math.BigDecimal;

import static online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/banking/v1", produces = APPLICATION_JSON_VALUE)
public class BankingControllerImpl implements BankingController {
    private @NonNull AccountService accountService;

    @PostMapping(value = "deposit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> deposit(@RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.deposit(
                    transactionRequest.account(), new BigDecimal(transactionRequest.amount()));
            return ResponseEntity.ok(new TransactionResponse(DEPOSIT_TRANSACTION_SUCCESSFUL));
        } catch (AccountException e) {
            return handleException(e);
        }
    }

    @PostMapping(value = "withdrawal", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody TransactionRequest transactionRequest) {
        try {
            accountService.withdraw(
                    transactionRequest.account(), new BigDecimal(transactionRequest.amount()));
            return ResponseEntity.ok(new TransactionResponse(WITHDRAWAL_TRANSACTION_SUCCESSFUL));
        } catch (AccountException e) {
            return handleException(e);
        }
    }

    @GetMapping("statement/{account}")
    public ResponseEntity<TransactionResponse> getStatement(@PathVariable("account") String accountNumber) {
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
            case ACCOUNT_NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TransactionResponse(ACCOUNT_NOT_FOUND));
            case INSUFFICIENT_FUNDS -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new TransactionResponse(INSUFFICIENT_FUNDS));
            case UNEXPECTED -> ResponseEntity.internalServerError()
                    .body(new TransactionResponse(SOMETHING_WENT_WRONG));
        };
    }
}
