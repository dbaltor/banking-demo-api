package online.dbaltor.demoapi.domain;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "of")
public class TransactionVO {
    private Long id;
    private @NonNull String date;
    private @NonNull BigDecimal amount;
    private @NonNull Type type;

    public static enum Type {
        DEPOSIT,
        WITHDRAWAL
    }
}
