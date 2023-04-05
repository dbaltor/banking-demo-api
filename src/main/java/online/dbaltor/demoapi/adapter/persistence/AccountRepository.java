package online.dbaltor.demoapi.adapter.persistence;

import lombok.RequiredArgsConstructor;
import online.dbaltor.demoapi.dto.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static online.dbaltor.demoapi.adapter.persistence.AccountDbException.ErrorType.*;

@Component
@RequiredArgsConstructor
public class AccountRepository {

    private final AccountDbRepository accDbRepository;

    public void addTransaction(String accountNumber, Transaction transaction) {
        try {
            accDbRepository.findByNumber(accountNumber)
                    .ifPresentOrElse(
                            accountDb -> accDbRepository.save(
                                    accountDb.addTransaction(TransactionDb.of(transaction))),
                            () -> accDbRepository.save(AccountDb.of(accountNumber)
                                    .addTransaction(TransactionDb.of(transaction)))
                    );
        } catch( Exception e) {
            throw AccountDbException.of(UNEXPECTED, Optional.of(e));
        }
    }


    public List<Transaction> retrieveAllTransactions(String accountNumber) {
        return accDbRepository.findByNumber(accountNumber)
                .map(acc -> {
                    return acc.getTransactions().stream()
                            .map(TransactionDb::transaction)
                            .toList();
                })
                .orElseThrow(() -> AccountDbException.of(ACCOUNT_NOT_FOUND, Optional.empty()));
    }
}
