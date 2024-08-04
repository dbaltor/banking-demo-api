package online.dbaltor.demoapi.adapter.persistence;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.ACCOUNT_NOT_FOUND;
import static online.dbaltor.demoapi.domain.TransactionTestHelper.*;
import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static online.dbaltor.demoapi.dto.Transaction.Type.WITHDRAWAL;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.val;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.dto.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest {
    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal TX_AMOUNT = new BigDecimal(100);

    @Mock private AccountDbRepository accountDbRepository;

    private AccountRepository accountRepository;

    @BeforeEach
    public void initialise() {
        accountRepository = new AccountRepository(accountDbRepository);
    }

    @Test
    public void shouldRejectDepositTransactionForNonExistentAccount() {
        // Given
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        AccountException exception =
                catchThrowableOfType(
                        AccountException.class,
                        () ->
                                accountRepository.addTransaction(
                                        ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT)));
        // Then
        assertThat(exception.errorType()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    public void shouldCreateAndStoreDepositTransaction() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(initialTransactionDb);
        val finalAccountDb =
                AccountDb.of(ACCOUNT_NUMBER)
                        .addTransaction(initialTransactionDb)
                        .addTransaction(TransactionDb.of(TODAY, TX_AMOUNT, DEPOSIT));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccountDb));
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
        // Then
        then(accountDbRepository).should().save(finalAccountDb);
    }

    @Test
    public void shouldRejectWithdrawalTransactionForNonExistentAccount() {
        // Given
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        AccountException exception =
                catchThrowableOfType(
                        AccountException.class,
                        () ->
                                accountRepository.addTransaction(
                                        ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT)));
        // Then
        assertThat(exception.errorType()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    public void shouldCreateAndStoreWithdrawalTransaction() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(initialTransactionDb);
        val finalAccountDb =
                AccountDb.of(ACCOUNT_NUMBER)
                        .addTransaction(initialTransactionDb)
                        .addTransaction(TransactionDb.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccountDb));
        // When
        accountRepository.addTransaction(
                ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        // Then
        then(accountDbRepository).should().save(finalAccountDb);
    }

    @Test
    public void shouldRetrieveAllTransactions() {
        // Given
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER);
        initialAccountDb.addTransaction(
                TransactionDb.of("01/12/2017", new BigDecimal("1000.00"), DEPOSIT));
        initialAccountDb.addTransaction(
                TransactionDb.of("10/12/2017", new BigDecimal("100.00"), WITHDRAWAL));
        initialAccountDb.addTransaction(
                TransactionDb.of("12/12/2017", new BigDecimal("500.00"), DEPOSIT));
        val initialTransactions =
                transactionsContaining(
                        deposit("01/12/2017", "1000.00"),
                        withdrawal("10/12/2017", "100.00"),
                        deposit("12/12/2017", "500.00"));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccountDb));
        // When
        val transactions = accountRepository.retrieveAllTransactions(ACCOUNT_NUMBER);
        // Then
        assertThat(transactions).hasSameElementsAs(initialTransactions);
    }
}
