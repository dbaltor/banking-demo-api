package online.dbaltor.demoapi.adapter.persistence;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.ACCOUNT_NOT_FOUND;
import static online.dbaltor.demoapi.domain.TransactionTestHelper.*;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.DEPOSIT;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.WITHDRAWAL;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.val;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.domain.TransactionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountVORepositoryTest {
    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal TX_AMOUNT = new BigDecimal(100);

    @Mock private AccountRepository accountRepository;

    private AccountVORepository accountVORepository;

    @BeforeEach
    public void initialise() {
        accountVORepository = new AccountVORepository(accountRepository);
    }

    @Test
    public void shouldRejectDepositTransactionForNonExistentAccount() {
        // Given
        given(accountRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        AccountException exception =
                catchThrowableOfType(
                        AccountException.class,
                        () ->
                                accountVORepository.addTransaction(
                                        ACCOUNT_NUMBER,
                                        TransactionVO.of(TODAY, TX_AMOUNT, DEPOSIT)));
        // Then
        assertThat(exception.errorType()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    public void shouldCreateAndStoreDepositTransaction() {
        // Given
        val initialTransaction = Transaction.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccount = Account.of(ACCOUNT_NUMBER).addTransaction(initialTransaction);
        val finalAccount =
                Account.of(ACCOUNT_NUMBER)
                        .addTransaction(initialTransaction)
                        .addTransaction(Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
        given(accountRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccount));
        // When
        accountVORepository.addTransaction(
                ACCOUNT_NUMBER, TransactionVO.of(TODAY, TX_AMOUNT, DEPOSIT));
        // Then
        then(accountRepository).should().save(finalAccount);
    }

    @Test
    public void shouldRejectWithdrawalTransactionForNonExistentAccount() {
        // Given
        given(accountRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        AccountException exception =
                catchThrowableOfType(
                        AccountException.class,
                        () ->
                                accountVORepository.addTransaction(
                                        ACCOUNT_NUMBER,
                                        TransactionVO.of(TODAY, TX_AMOUNT, DEPOSIT)));
        // Then
        assertThat(exception.errorType()).isEqualTo(ACCOUNT_NOT_FOUND);
    }

    @Test
    public void shouldCreateAndStoreWithdrawalTransaction() {
        // Given
        val initialTransaction = Transaction.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccount = Account.of(ACCOUNT_NUMBER).addTransaction(initialTransaction);
        val finalAccount =
                Account.of(ACCOUNT_NUMBER)
                        .addTransaction(initialTransaction)
                        .addTransaction(Transaction.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        given(accountRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccount));
        // When
        accountVORepository.addTransaction(
                ACCOUNT_NUMBER, TransactionVO.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        // Then
        then(accountRepository).should().save(finalAccount);
    }

    @Test
    public void shouldRetrieveAllTransactions() {
        // Given
        val initialAccount = Account.of(ACCOUNT_NUMBER);
        initialAccount.addTransaction(
                Transaction.of("01/12/2017", new BigDecimal("1000.00"), DEPOSIT));
        initialAccount.addTransaction(
                Transaction.of("10/12/2017", new BigDecimal("100.00"), WITHDRAWAL));
        initialAccount.addTransaction(
                Transaction.of("12/12/2017", new BigDecimal("500.00"), DEPOSIT));
        val initialTransactions =
                transactionsContaining(
                        deposit("01/12/2017", "1000.00"),
                        withdrawal("10/12/2017", "100.00"),
                        deposit("12/12/2017", "500.00"));
        given(accountRepository.findByNumber(ACCOUNT_NUMBER))
                .willReturn(Optional.of(initialAccount));
        // When
        val transactions = accountVORepository.retrieveAllTransactions(ACCOUNT_NUMBER);
        // Then
        assertThat(transactions).hasSameElementsAs(initialTransactions);
    }
}
