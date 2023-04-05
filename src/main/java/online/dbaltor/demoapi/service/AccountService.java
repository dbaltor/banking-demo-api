package online.dbaltor.demoapi.service;

import lombok.RequiredArgsConstructor;
import online.dbaltor.demoapi.util.MyClock;
import online.dbaltor.demoapi.dto.Transaction;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static online.dbaltor.demoapi.dto.Transaction.Type.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final StatementPrinterService statementPrinterService;
    private final MyClock clock;

    public void deposit(String accountNumber, BigDecimal amount) {
        accountRepository.addTransaction(accountNumber, Transaction.of(clock.todayAsString(), amount, DEPOSIT));
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        accountRepository.addTransaction(accountNumber, Transaction.of(clock.todayAsString(), amount, WITHDRAWAL));
    }

    public String getStatement(String accountNumber) {
        var transactions = accountRepository.retrieveAllTransactions(accountNumber);
        return statementPrinterService.print(transactions);
    }
}