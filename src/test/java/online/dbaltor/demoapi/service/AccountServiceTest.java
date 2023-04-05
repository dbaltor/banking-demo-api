package online.dbaltor.demoapi.service;

import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import online.dbaltor.demoapi.dto.Transaction;
import online.dbaltor.demoapi.util.MyClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static online.dbaltor.demoapi.dto.Transaction.Type.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal AMOUNT = new BigDecimal(100);
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
        accountService.deposit(ACCOUNT_NUMBER, AMOUNT);
        // Then
        verify(accountRepository).addTransaction("1", Transaction.of(TODAY, AMOUNT, DEPOSIT));
    }

    @Test
    void shouldStoreWithdrawalTransaction() {
        // Given
        given(clock.todayAsString()).willReturn(TODAY);
        // When
        accountService.withdraw(ACCOUNT_NUMBER, AMOUNT);
        // Then
        verify(accountRepository).addTransaction("1", Transaction.of(TODAY, AMOUNT, WITHDRAWAL));
    }

    @Test
    void shouldPrintStatement() {
        // Given
        List<Transaction> transactions = List.of(Transaction.of("23/03/2023", AMOUNT, DEPOSIT));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        accountService.getStatement(ACCOUNT_NUMBER);
        // Then
        verify(statementPrinterService).print(transactions);
    }
}