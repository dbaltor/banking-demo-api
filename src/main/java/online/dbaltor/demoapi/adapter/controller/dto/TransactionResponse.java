package online.dbaltor.demoapi.adapter.controller.dto;

import lombok.*;

@Data @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TransactionResponse {
    private @NonNull String message;
}