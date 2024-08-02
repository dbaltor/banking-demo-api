package online.dbaltor.demoapi.adapter.persistence;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.*;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.dto.Transaction;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountRepository {

    private final AccountDbRepository accDbRepository;

    public void addTransaction(String accountNumber, Transaction transaction) {
        try {
            val accountDb =
                    accDbRepository
                            .findByNumber(accountNumber)
                            .orElseThrow(
                                    () -> AccountException.of(ACCOUNT_NOT_FOUND, Optional.empty()));
            accDbRepository.save(accountDb.addTransaction(TransactionDb.of(transaction)));
        } catch (AccountException accountException) {
            throw accountException;
        } catch (Exception e) {
            throw AccountException.of(UNEXPECTED, Optional.of(e));
        }
    }

    public List<Transaction> retrieveAllTransactions(String accountNumber) {
        return accDbRepository
                .findByNumber(accountNumber)
                .map(
                        acc -> {
                            return acc.getTransactions().stream()
                                    .map(TransactionDb::transaction)
                                    .toList();
                        })
                .orElseThrow(() -> AccountException.of(ACCOUNT_NOT_FOUND, Optional.empty()));
    }
}
