package online.dbaltor.demoapi.application;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.INSUFFICIENT_FUNDS;
import static online.dbaltor.demoapi.domain.TransactionTestHelper.deposit;
import static online.dbaltor.demoapi.domain.TransactionTestHelper.transactionsContaining;
import static online.dbaltor.demoapi.dto.Transaction.Type.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import lombok.val;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import online.dbaltor.demoapi.domain.StatementPrinterService;
import online.dbaltor.demoapi.dto.Transaction;
import online.dbaltor.demoapi.util.MyClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal TX_AMOUNT = new BigDecimal(100);
    @Mock private MyClock clock;
    @Mock private StatementPrinterService statementPrinterService;
    @Mock private AccountRepository accountRepository;
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
        then(accountRepository)
                .should()
                .addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
    }

    @Test
    void shouldStoreWithdrawalTransaction() {
        // Given
        given(clock.todayAsString()).willReturn(TODAY);
        val transactions = transactionsContaining(deposit("23/03/2023", TX_AMOUNT.toString()));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        accountService.withdraw(ACCOUNT_NUMBER, TX_AMOUNT);
        // Then
        then(accountRepository)
                .should()
                .addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, WITHDRAWAL));
    }

    @Test
    void shouldRejectWithdrawalTransaction() {
        // Given
        val transactions = transactionsContaining(deposit("23/03/2023", TX_AMOUNT.toString()));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        AccountException exception =
                assertThrows(
                        AccountException.class,
                        () -> {
                            accountService.withdraw(ACCOUNT_NUMBER, TX_AMOUNT.add(BigDecimal.ONE));
                        });
        // Then
        assertThat(exception.errorType()).isEqualTo(INSUFFICIENT_FUNDS);
    }

    @Test
    void shouldPrintStatement() {
        // Given
        val transactions = transactionsContaining(deposit("23/03/2023", TX_AMOUNT.toString()));
        given(accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER)).willReturn(transactions);
        // When
        accountService.getStatement(ACCOUNT_NUMBER);
        // Then
        then(statementPrinterService).should().print(transactions);
    }
}
