package online.dbaltor.demoapi.domain;

import static java.util.stream.Collectors.*;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.demoapi.util.AtomicBigDecimal;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatementPrinterService {
    private static final String STATEMENT_HEADER = "DATE       | AMOUNT | BALANCE\n";
    private static DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    public String print(List<TransactionVO> transactions) {
        return STATEMENT_HEADER + printStatementLines(transactions);
    }

    private String printStatementLines(List<TransactionVO> transactions) {
        AtomicBigDecimal runningBalance =
                new AtomicBigDecimal(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN));

        return transactions.stream()
                .map(transaction -> statementLine(transaction, runningBalance))
                .collect(
                        collectingAndThen(
                                toCollection(ArrayList::new),
                                array -> {
                                    Collections.reverse(array);
                                    return array.stream();
                                }))
                .collect(joining("\n"));
    }

    private static String statementLine(
            TransactionVO transaction, AtomicBigDecimal runningBalance) {
        val amount =
                transaction.getType() == DEPOSIT
                        ? transaction.getAmount()
                        : transaction.getAmount().negate();
        return "%s | %s | %s"
                .formatted(
                        transaction.getDate(),
                        decimalFormatter.format(amount),
                        decimalFormatter.format(runningBalance.addAndGet(amount)));
    }
}
