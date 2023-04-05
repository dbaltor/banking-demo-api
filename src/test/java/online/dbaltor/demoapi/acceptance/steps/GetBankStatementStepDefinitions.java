package online.dbaltor.demoapi.acceptance.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import lombok.val;
import online.dbaltor.demoapi.service.StatementPrinterService;
import online.dbaltor.demoapi.util.MyClock;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;

public class GetBankStatementStepDefinitions extends CucumberSpringContextConfig implements En {
    @Autowired private MyClock clock;
    private String accountNumber;
    private String statement;
    private BankingClient bankingClient;

    public GetBankStatementStepDefinitions() {
        Before(() -> bankingClient = new BankingClient(getServerPort()));

        Given("Jane has a bank account number {word}", (String number) -> accountNumber = number);

        And("she deposits {word} GBP on {word}", (String amount, String date) -> {
            given(clock.todayAsString()).willReturn(date);
            bankingClient.deposit(accountNumber, amount);
        });

        And("she withdraws {word} GBP on {word}", (String amount, String date) -> {
            given(clock.todayAsString()).willReturn(date);
            bankingClient.withdraw(accountNumber, amount);
        }) ;

        When("she gets her bank statement", () -> statement = bankingClient.getStatement(accountNumber));

        Then("she should be shown the following:", (DataTable dataTable ) -> {
            val expectedStatement = StatementPrinterService.STATEMENT_HEADER + statementLines(dataTable.asMaps());
            assertThat(statement, is(expectedStatement));
        });
    }

    private static String statementLines(List<Map<String, String>> statementLines) {
        return statementLines.stream()
            .map(m -> "%s | %s | %s".formatted(m.get("DATE"), m.get("AMOUNT"), m.get("BALANCE")))
            .collect(joining("\n"));
    }
}
