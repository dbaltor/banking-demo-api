package online.dbaltor.demoapi.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Account {
    private Long id;
    private @NonNull String number;
    private Set<Transaction> transactions = new HashSet<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
