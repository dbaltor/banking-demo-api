package online.dbaltor.demoapi.adapter.persistence;

import lombok.*;
import online.dbaltor.demoapi.dto.Account;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.HashSet;
import java.util.Set;

@Data @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class AccountDb {
    private @Setter @Id Long id;
    private @NonNull String number;

    @MappedCollection(idColumn = "ACCOUNT_ID")
    private Set<TransactionDb> transactions = new HashSet<>();

    public AccountDb addTransaction(TransactionDb transactionDb) {
        transactions.add(transactionDb);
        return this;
    }

    public Account account() {
        val acc = Account.of(
                this.number);
        acc.setId(this.id);
        transactions.stream()
            .map(TransactionDb::transaction)
            .forEach(acc::addTransaction);
        return acc;
    }

    public static AccountDb of(Account acc) {
        val accDb = AccountDb.of(
                acc.getNumber());
        accDb.setId(acc.getId());
        acc.getTransactions().stream()
            .map(TransactionDb::of)
            .forEach(accDb::addTransaction);
        return accDb;
    }
}
