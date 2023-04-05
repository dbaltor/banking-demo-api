package online.dbaltor.demoapi.adapter.persistence;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;

@Getter @Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class AccountDbException extends RuntimeException{
    public static enum ErrorType {
        ACCOUNT_NOT_FOUND,
        UNEXPECTED
    }

    private @NonNull ErrorType errorType;
    private @NonNull Optional<Exception> error;
}