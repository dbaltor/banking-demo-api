package online.dbaltor.demoapi.dto;

import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Transaction {
    private Long id;
    private @NonNull String date;
    private @NonNull BigDecimal amount;
    private @NonNull Type type;

    public static enum Type {
        DEPOSIT,
        WITHDRAWAL
    }
}
