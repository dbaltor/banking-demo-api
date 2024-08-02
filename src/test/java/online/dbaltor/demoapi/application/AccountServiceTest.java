package online.dbaltor.demoapi.application;

import lombok.val;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import online.dbaltor.demoapi.domain.StatementPrinterService;
import online.dbaltor.demoapi.dto.Transaction;
import online.dbaltor.demoapi.util.MyClock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.INSUFFICIENT_FUNDS;
import static online.dbaltor.demoapi.dto.Transaction.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal TX_AMOUNT = new BigDecimal(100);
    @Mock
    private MyClock clock;
    @Mock
    private StatementPrinterService statementPrinterService;
    @Mock
    private AccountRepository accountRepository;
    private AccountService accountService;


    @BeforeEach
    public void initialise() {
        accountService = new AccountService(accountRepository, statementPrinterService, clock);
    }

    @Test
    void shouldStoreDepositTransaction() {
        // Given
        given(clock.todayAsString()).willReturn(TODAY);
        // When
        accountService.deposit(ACCOUNT_NUMBER, TX_AMOUNT);
        // Then
        then(accountRepository).should().addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
    }

    @Test
    void shouldStoreWithdrawalTransaction() {
        // Given
        given(clock.todayAsString()).willReturn(TODAY);
        val transactions = List.of(Transaction.of("23/03/2023", TX_AMOUNT, DEPOSIT));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        accountService.withdraw(ACCOUNT_NUMBER, TX_AMOUNT);
        // Then
        then(accountRepository).should().addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, WITHDRAWAL));
    }

    @Test
    void shouldRejectWithdrawalTransaction() {
        // Given
        val transactions = List.of(Transaction.of("23/03/2023", TX_AMOUNT, DEPOSIT));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        AccountException exception = assertThrows(AccountException.class, () -> {
            accountService.withdraw(ACCOUNT_NUMBER, TX_AMOUNT.add(BigDecimal.ONE));
        });
        // Then
        Assertions.assertEquals(INSUFFICIENT_FUNDS, exception.errorType());
    }

    @Test
    void shouldPrintStatement() {
        // Given
        val transactions = List.of(Transaction.of("23/03/2023", TX_AMOUNT, DEPOSIT));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        accountService.getStatement(ACCOUNT_NUMBER);
        // Then
        then(statementPrinterService).should().print(transactions);
    }
}