package online.dbaltor.demoapi.domain;

import static online.dbaltor.demoapi.domain.TransactionVO.Type.DEPOSIT;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.WITHDRAWAL;

import java.math.BigDecimal;
import java.util.List;

public class TransactionTestHelper {
    public static List<TransactionVO> transactionsContaining(TransactionVO... transactions) {
        return List.of(transactions);
    }

    public static TransactionVO deposit(String date, String amount) {
        return TransactionVO.of(date, new BigDecimal(amount), DEPOSIT);
    }

    public static TransactionVO withdrawal(String date, String amount) {
        return TransactionVO.of(date, new BigDecimal(amount), WITHDRAWAL);
    }
}
