package online.dbaltor.demoapi.domain;

import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class AccountVO {
    private Long id;
    private @NonNull String number;
    private Set<TransactionVO> transactions = new HashSet<>();

    public void addTransaction(TransactionVO transaction) {
        transactions.add(transaction);
    }
}
