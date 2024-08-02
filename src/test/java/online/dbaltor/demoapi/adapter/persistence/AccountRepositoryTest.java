package online.dbaltor.demoapi.adapter.persistence;

import lombok.val;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.dto.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.ACCOUNT_NOT_FOUND;
import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static online.dbaltor.demoapi.dto.Transaction.Type.WITHDRAWAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        AccountException exception = assertThrows(AccountException.class, () -> {
            accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
        });
        // Then
        Assertions.assertEquals(ACCOUNT_NOT_FOUND, exception.errorType());
    }

    @Test
    public void shouldCreateAndStoreDepositTransactionForAccount() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER ).addTransaction(initialTransactionDb);
        val finalAccountDb = AccountDb.of(ACCOUNT_NUMBER)
                .addTransaction(initialTransactionDb)
                .addTransaction(TransactionDb.of(TODAY, TX_AMOUNT, DEPOSIT));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.of(initialAccountDb));
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
        AccountException exception = assertThrows(AccountException.class, () -> {
            accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, DEPOSIT));
        });
        // Then
        assertEquals(ACCOUNT_NOT_FOUND, exception.errorType());
    }

    @Test
    public void shouldCreateAndStoreWithdrawalTransactionForAccount() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(initialTransactionDb);
        val finalAccountDb = AccountDb.of(ACCOUNT_NUMBER)
                .addTransaction(initialTransactionDb)
                .addTransaction(TransactionDb.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.of(initialAccountDb));
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, TX_AMOUNT, WITHDRAWAL));
        // Then
        then(accountDbRepository).should().save(finalAccountDb);
    }
}
