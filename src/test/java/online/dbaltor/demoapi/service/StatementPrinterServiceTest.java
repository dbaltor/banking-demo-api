package online.dbaltor.demoapi.service;

import lombok.val;
import online.dbaltor.demoapi.dto.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static online.dbaltor.demoapi.dto.Transaction.Type.WITHDRAWAL;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StatementPrinterServiceTest {
    private static final List<Transaction> NO_TRANSACTIONS = List.of();
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
        assertThat(statement, is("DATE       | AMOUNT | BALANCE\n"));
    }

    @Test
    public void shouldPrintTransactionsInReverseChronologicalOrder() {
        //Given
        val expectedStatement = """
                DATE       | AMOUNT | BALANCE
                12/12/2017 | 500.00 | 1400.00
                10/12/2017 | -100.00 | 900.00
                01/12/2017 | 1000.00 | 1000.00""";
        val transactions = transactionsContaining(
                deposit("01/12/2017", "1000.00"),
                withdrawal("10/12/2017", "100.00"),
                deposit("12/12/2017", "500.00"));
        // When
        val statement = statementPrinterService.print(transactions);
        // Then
        assertThat(statement, is(expectedStatement));
    }

    private static List<Transaction> transactionsContaining(Transaction... transactions) {
        return List.of(transactions);
    }
    private static Transaction deposit(String date, String amount) {
        return Transaction.of(date, new BigDecimal(amount), DEPOSIT);
    }
    private static Transaction withdrawal(String date, String amount) {
        return Transaction.of(date, new BigDecimal(amount), WITHDRAWAL);
    }

}
