package online.dbaltor.demoapi.domain;

import static online.dbaltor.demoapi.domain.TransactionTestHelper.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatementPrinterServiceTest {
    private static final List<TransactionVO> NO_TRANSACTIONS = List.of();
    private StatementPrinterService statementPrinterService;

    @BeforeEach
    public void initialise() {
        statementPrinterService = new StatementPrinterService();
    }

    @Test
    public void shouldAlwaysPrintTheHeader() {
        // When
        val statement = statementPrinterService.print(NO_TRANSACTIONS);
        // Then
        assertThat(statement).isEqualTo("DATE       | AMOUNT | BALANCE\n");
    }

    @Test
    public void shouldPrintTransactionsInReverseChronologicalOrder() {
        // Given
        val expectedStatement =
                """
                DATE       | AMOUNT | BALANCE
                12/12/2017 | 500.00 | 1400.00
                10/12/2017 | -100.00 | 900.00
                01/12/2017 | 1000.00 | 1000.00""";
        val transactions =
                transactionsContaining(
                        deposit("01/12/2017", "1000.00"),
                        withdrawal("10/12/2017", "100.00"),
                        deposit("12/12/2017", "500.00"));
        // When
        val statement = statementPrinterService.print(transactions);
        // Then
        assertThat(statement).isEqualTo(expectedStatement);
    }
}
