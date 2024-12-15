package online.dbaltor.demoapi.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Generated;
import org.springframework.stereotype.Component;

@Generated
@Component
public class MyClock {
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String todayAsString() {
        return LocalDate.now().format(DD_MM_YYYY);
    }
}
