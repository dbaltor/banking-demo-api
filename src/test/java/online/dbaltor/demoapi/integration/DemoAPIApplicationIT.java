package online.dbaltor.demoapi.integration;

import static online.dbaltor.demoapi.domain.TransactionVO.Type.DEPOSIT;
import static online.dbaltor.demoapi.domain.TransactionVO.Type.WITHDRAWAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import online.dbaltor.demoapi.adapter.persistence.Account;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import online.dbaltor.demoapi.adapter.persistence.AccountVORepository;
import online.dbaltor.demoapi.domain.AccountVO;
import online.dbaltor.demoapi.domain.TransactionVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
public class DemoAPIApplicationIT {
    private static final String BASE_URL = "/banking/v1";

    @LocalServerPort private Integer serverPort;

    @Container @ServiceConnection
    private static final MySQLContainer<?> mysql =
            new MySQLContainer<>(DockerImageName.parse("mysql:9.1.0"));

    private final String accountNumber = "654321";
    private final String transactionAmount = "100.00";
    private Account account;

    private WebTestClient testClient() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + serverPort).build();
    }

    @Autowired private AccountVORepository accountVORepository;
    @Autowired private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        val account = AccountVO.of(accountNumber);
        account.addTransaction(
                TransactionVO.of(
                        "23/03/2023",
                        new BigDecimal(transactionAmount).add(BigDecimal.ONE),
                        DEPOSIT));
        this.account = accountRepository.save(Account.of(account));
    }

    @AfterEach
    void tearDown() {
        accountRepository.delete(account);
    }

    @Test
    void shouldSaveDepositTransactionOnDatabaseAndReturn200WhenDepositRequestIsSuccessful() {
        // Given
        val transactionRequest = new TransactionRequest(accountNumber, transactionAmount);

        testClient()
                .post()
                .uri(BASE_URL + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                // When
                .exchange()
                // Then
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON_VALUE)
                .expectBody(TransactionResponse.class)
                .isEqualTo(new TransactionResponse("Deposit transaction successful"));

        val transactions = accountVORepository.retrieveAllTransactions(accountNumber);
        assertThat(transactions, hasSize(2));
        assertThat(transactions.get(1).getAmount().toString(), is(transactionAmount));
        assertThat(transactions.get(1).getType(), is(DEPOSIT));
    }

    @Test
    void shouldSaveWithdrawTransactionOnDatabaseAndReturn200WhenWithdrawRequestIsSuccessful() {
        // Given
        val transactionRequest = new TransactionRequest(accountNumber, transactionAmount);

        testClient()
                .post()
                .uri(BASE_URL + "/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                // When
                .exchange()
                // Then
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(APPLICATION_JSON_VALUE)
                .expectBody(TransactionResponse.class)
                .isEqualTo(new TransactionResponse("Withdrawal transaction successful"));

        val transactions = accountVORepository.retrieveAllTransactions(accountNumber);
        assertThat(transactions, hasSize(2));
        assertThat(transactions.get(1).getAmount().toString(), is(transactionAmount));
        assertThat(transactions.get(1).getType(), is(WITHDRAWAL));
    }
}
