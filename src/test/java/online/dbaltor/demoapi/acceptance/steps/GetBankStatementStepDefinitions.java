package online.dbaltor.demoapi.acceptance.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

public class GetBankStatementStepDefinitions extends CucumberSpringContextConfig {
    @Autowired private MyClock clock;
    private String accountNumber;
    private String statement;
    private BankingClient bankingClient;

    @Given("Jane has a bank account number {word}")
    public void janeHasABankAccountNumber(String number) {
        accountNumber = number;
        bankingClient = new BankingClient(getServerPort());
    }

    @And("she deposits {word} GBP on {word}")
    public void sheDepositsGBPOn(String amount, String date) throws Exception {
        given(clock.todayAsString()).willReturn(date);
        bankingClient.deposit(accountNumber, amount);
    }

    @And("she withdraws {word} GBP on {word}")
    public void sheWithdrawsGBPOn(String amount, String date) throws Exception {
        given(clock.todayAsString()).willReturn(date);
        bankingClient.withdraw(accountNumber, amount);
    }

    @When("she gets her bank statement")
    public void sheGetsHerBankStatement() throws Exception {
        statement = bankingClient.getStatement(accountNumber);
    }

    @Then("she should be shown the following:")
    public void sheShouldBeShownTheFollowing(List<Map<String,String>> statement) {
        val expectedStatement = StatementPrinterService.STATEMENT_HEADER + statementLines(statement);

        assertThat(this.statement, is(expectedStatement));
    }

    private static String statementLines(List<Map<String, String>> statementLines) {
        return statementLines.stream()
            .map(m -> "%s | %s | %s".formatted(m.get("DATE"), m.get("AMOUNT"), m.get("BALANCE")))
            .collect(joining("\n"));
    }
}
