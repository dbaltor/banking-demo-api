package online.dbaltor.demoapi.adapter.persistence;

import lombok.val;
import online.dbaltor.demoapi.dto.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static online.dbaltor.demoapi.dto.Transaction.Type.WITHDRAWAL;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest {
    private static final String TODAY = "01/04/2023";
    private static final String ACCOUNT_NUMBER = "1";
    private static final BigDecimal AMOUNT = new BigDecimal(100);

    @Mock private AccountDbRepository accountDbRepository;

    private AccountRepository accountRepository;

    @BeforeEach
    public void initialise() {
        accountRepository = new AccountRepository(accountDbRepository);
    }

    @Test
    public void shouldCreateAndStoreDepositTransactionForNewAccount() {
        // Given
        val accountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(TransactionDb.of(TODAY, AMOUNT, DEPOSIT));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, AMOUNT, DEPOSIT));
        // Then
        verify(accountDbRepository).save(accountDb);
    }

    @Test
    public void shouldCreateAndStoreDepositTransactionForExistingAccount() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), DEPOSIT);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER ).addTransaction(initialTransactionDb);
        val finalAccountDb = AccountDb.of(ACCOUNT_NUMBER)
                .addTransaction(initialTransactionDb)
                .addTransaction(TransactionDb.of(TODAY, AMOUNT, DEPOSIT));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.of(initialAccountDb));
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, AMOUNT, DEPOSIT));
        // Then
        verify(accountDbRepository).save(finalAccountDb);
    }

    @Test
    public void createAndStoreWithdrawalTransactionForNewAccount() {
        // Given
        val accountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(TransactionDb.of(TODAY, AMOUNT, WITHDRAWAL));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.empty());
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, AMOUNT, WITHDRAWAL));
        // Then
        verify(accountDbRepository).save(accountDb);
    }

    @Test
    public void shouldCreateAndStoreWithdrawalTransactionForExistingAccount() {
        // Given
        val initialTransactionDb = TransactionDb.of("01/03/2023", new BigDecimal(500), WITHDRAWAL);
        val initialAccountDb = AccountDb.of(ACCOUNT_NUMBER).addTransaction(initialTransactionDb);
        val finalAccountDb = AccountDb.of(ACCOUNT_NUMBER)
                .addTransaction(initialTransactionDb)
                .addTransaction(TransactionDb.of(TODAY, AMOUNT, WITHDRAWAL));
        given(accountDbRepository.findByNumber(ACCOUNT_NUMBER)).willReturn(Optional.of(initialAccountDb));
        // When
        accountRepository.addTransaction(ACCOUNT_NUMBER, Transaction.of(TODAY, AMOUNT, WITHDRAWAL));
        // Then
        verify(accountDbRepository).save(finalAccountDb);
    }
}
