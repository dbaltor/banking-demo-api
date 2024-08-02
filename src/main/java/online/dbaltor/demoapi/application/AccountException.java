package online.dbaltor.demoapi.application;

import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class AccountException extends RuntimeException {
    public static enum ErrorType {
        ACCOUNT_NOT_FOUND,
        INSUFFICIENT_FUNDS,
        UNEXPECTED
    }

    private @NonNull ErrorType errorType;
    private @NonNull Optional<Exception> error;
}
