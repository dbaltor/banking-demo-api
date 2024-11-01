package online.dbaltor.demoapi.acceptance.steps;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import java.util.List;
import java.util.Map;
import lombok.val;
import online.dbaltor.demoapi.acceptance.spring.CucumberSpringContextConfig;
import online.dbaltor.demoapi.acceptance.steps.apis.BankingAPI;
import online.dbaltor.demoapi.adapter.persistence.Account;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import online.dbaltor.demoapi.domain.AccountVO;
import online.dbaltor.demoapi.util.MyClock;
import org.springframework.beans.factory.annotation.Autowired;

public class GetBankStatementStepDefinitions extends CucumberSpringContextConfig implements En {
    private static final String STATEMENT_HEADER = "DATE       | AMOUNT | BALANCE\n";

    @Autowired private MyClock clock;
    @Autowired private AccountRepository accountRepository;

    private BankingAPI bankingAPI;
    private String accountNumber;
    private String statement;

    public GetBankStatementStepDefinitions() {

        Before(
                () -> {
                    bankingAPI = new BankingAPI(getServerPort());
                    accountRepository.deleteAll();
                });

        Given(
                "Jane has a bank account with number {word}",
                (String number) -> {
                    this.accountNumber = number;
                    accountRepository.save(Account.of(AccountVO.of(accountNumber)));
                });

        And(
                "she deposits {word} GBP on {word}",
                (String amount, String date) -> {
                    given(clock.todayAsString()).willReturn(date);
                    bankingAPI.deposit(accountNumber, amount);
                });

        And(
                "she withdraws {word} GBP on {word}",
                (String amount, String date) -> {
                    given(clock.todayAsString()).willReturn(date);
                    bankingAPI.withdraw(accountNumber, amount);
                });

        When(
                "she gets her bank statement",
                () -> statement = bankingAPI.getStatement(accountNumber));

        Then(
                "she should be shown the following:",
                (DataTable dataTable) -> {
                    val expectedStatement = STATEMENT_HEADER + statementLines(dataTable.asMaps());
                    assertThat(statement).isEqualTo(expectedStatement);
                });
    }

    private static String statementLines(List<Map<String, String>> statementLines) {
        return statementLines.stream()
                .map(
                        m ->
                                "%s | %s | %s"
                                        .formatted(
                                                m.get("DATE"), m.get("AMOUNT"), m.get("BALANCE")))
                .collect(joining("\n"));
    }
}
