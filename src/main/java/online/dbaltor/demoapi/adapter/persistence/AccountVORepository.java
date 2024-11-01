package online.dbaltor.demoapi.adapter.persistence;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.*;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.domain.TransactionVO;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountVORepository {

    private final AccountRepository accRepository;

    public void addTransaction(String accountNumber, TransactionVO transaction) {
        try {
            val account =
                    accRepository
                            .findByNumber(accountNumber)
                            .orElseThrow(
                                    () -> AccountException.of(ACCOUNT_NOT_FOUND, Optional.empty()));
            accRepository.save(account.addTransaction(Transaction.of(transaction)));
        } catch (AccountException accountException) {
            throw accountException;
        } catch (Exception e) {
            throw AccountException.of(UNEXPECTED, Optional.of(e));
        }
    }

    public List<TransactionVO> retrieveAllTransactions(String accountNumber) {
        return accRepository
                .findByNumber(accountNumber)
                .map(
                        acc -> {
                            return acc.getTransactions().stream()
                                    .map(Transaction::transaction)
                                    .toList();
                        })
                .orElseThrow(() -> AccountException.of(ACCOUNT_NOT_FOUND, Optional.empty()));
    }
}
