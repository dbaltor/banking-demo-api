package online.dbaltor.demoapi.domain;

import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static online.dbaltor.demoapi.dto.Transaction.Type.WITHDRAWAL;

import java.math.BigDecimal;
import java.util.List;
import online.dbaltor.demoapi.dto.Transaction;

public class TransactionTestHelper {
    public static List<Transaction> transactionsContaining(Transaction... transactions) {
        return List.of(transactions);
    }

    public static Transaction deposit(String date, String amount) {
        return Transaction.of(date, new BigDecimal(amount), DEPOSIT);
    }

    public static Transaction withdrawal(String date, String amount) {
        return Transaction.of(date, new BigDecimal(amount), WITHDRAWAL);
    }
}
