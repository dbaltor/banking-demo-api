package online.dbaltor.demoapi.adapter.persistence;

import java.math.BigDecimal;
import lombok.*;
import online.dbaltor.demoapi.dto.Transaction;
import org.springframework.data.annotation.Id;

import static online.dbaltor.demoapi.dto.Transaction.*;

@Data @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TransactionDb {
    private @Setter @Id Long id;
    private @NonNull String date;
    private @NonNull BigDecimal amount;
    private @NonNull Type type;

    public Transaction transaction() {
        val tx = Transaction.of(
                this.date,
                this.amount,
                this.type);
        tx.setId(this.id);
        return tx;

    }

    public static TransactionDb of(Transaction tx) {
        val txDb = TransactionDb.of(
                tx.getDate(),
                tx.getAmount(),
                tx.getType());
        txDb.setId(tx.getId());
        return txDb;
    }

}