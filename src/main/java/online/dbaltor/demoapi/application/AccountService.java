package online.dbaltor.demoapi.application;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.INSUFFICIENT_FUNDS;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.*;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.demoapi.adapter.persistence.AccountVORepository;
import online.dbaltor.demoapi.domain.StatementPrinterService;
import online.dbaltor.demoapi.domain.TransactionVO;
import online.dbaltor.demoapi.util.MyClock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountVORepository accountVORepository;
    private final StatementPrinterService statementPrinterService;
    private final MyClock clock;

    public void deposit(String accountNumber, BigDecimal amount) {
        accountVORepository.addTransaction(
                accountNumber, TransactionVO.of(clock.todayAsString(), amount, DEPOSIT));
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        val accountBalance =
                accountVORepository.retrieveAllTransactions(accountNumber).stream()
                        .map(
                                tx ->
                                        tx.getType() == DEPOSIT
                                                ? tx.getAmount()
                                                : tx.getAmount().negate())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (amount.compareTo(accountBalance) > 0)
            throw AccountException.of(INSUFFICIENT_FUNDS, Optional.empty());
        accountVORepository.addTransaction(
                accountNumber, TransactionVO.of(clock.todayAsString(), amount, WITHDRAWAL));
    }

    public String getStatement(String accountNumber) {
        var transactions = accountVORepository.retrieveAllTransactions(accountNumber);
        return statementPrinterService.print(transactions);
    }
}
