package online.dbaltor.demoapi.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Generated
@Component
public class MyClock {
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String todayAsString() {
        return LocalDate.now().format(DD_MM_YYYY);
    }

}
