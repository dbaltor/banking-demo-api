package online.dbaltor.demoapi.adapter.persistence;

import static online.dbaltor.demoapi.domain.TransactionVO.*;

import java.math.BigDecimal;
import lombok.*;
import online.dbaltor.demoapi.domain.TransactionVO;
import online.dbaltor.demoapi.util.ExcludeFromJacocoGeneratedReport;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class Transaction {
    private @Setter @Id Long id;
    private @NonNull String date;
    private @NonNull BigDecimal amount;
    private @NonNull Type type;

    @ExcludeFromJacocoGeneratedReport
    public TransactionVO transaction() {
        val transactionVO = TransactionVO.of(this.date, this.amount, this.type);
        transactionVO.setId(this.id);
        return transactionVO;
    }

    @ExcludeFromJacocoGeneratedReport
    public static Transaction of(TransactionVO transactionVO) {
        val transaction =
                Transaction.of(
                        transactionVO.getDate(),
                        transactionVO.getAmount(),
                        transactionVO.getType());
        transaction.setId(transactionVO.getId());
        return transaction;
    }
}
