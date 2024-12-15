package online.dbaltor.demoapi.adapter.persistence;

import java.util.HashSet;
import java.util.Set;
import lombok.*;
import online.dbaltor.demoapi.domain.AccountVO;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class Account {
    private @Setter @Id Long id;
    private @NonNull String number;

    @MappedCollection(idColumn = "ACCOUNT_ID")
    private Set<Transaction> transactions = new HashSet<>();

    public Account addTransaction(Transaction transaction) {
        transactions.add(transaction);
        return this;
    }

    @Generated
    public AccountVO account() {
        val accountVO = AccountVO.of(this.number);
        accountVO.setId(this.id);
        transactions.stream().map(Transaction::transaction).forEach(accountVO::addTransaction);
        return accountVO;
    }

    @Generated
    public static Account of(AccountVO accountVO) {
        val account = Account.of(accountVO.getNumber());
        account.setId(accountVO.getId());
        accountVO.getTransactions().stream().map(Transaction::of).forEach(account::addTransaction);
        return account;
    }
}
