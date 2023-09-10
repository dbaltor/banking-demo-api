package online.dbaltor.demoapi.integration;

import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import online.dbaltor.demoapi.adapter.persistence.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static online.dbaltor.demoapi.dto.Transaction.Type.DEPOSIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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
public class BankingControllerIT {
    private static final String BASE_URL = "/banking/v1";
    @LocalServerPort
    private Integer serverPort;
    private WebTestClient testClient() {
        return WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + serverPort)
                .build();
    }
    @Autowired
    private AccountRepository accountRepository;
    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"));

    @Test
    void shouldSaveDepositTransactionOnDatabaseAndReturn200WhenDepositRequestIsSuccessful() {
        // Given
        val accountNumber = "123456";
        val transactionAmount = "100.00";
        val transactionRequest = new TransactionRequest(accountNumber, transactionAmount);

        testClient()
                .post()
                .uri(BASE_URL + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), TransactionRequest.class)
        // When
                .exchange()
        // Then
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON_VALUE)
                .expectBody(TransactionResponse.class).isEqualTo(new TransactionResponse("Deposit transaction successful"));

        val transactions = accountRepository.retrieveAllTransactions(accountNumber);
        assertThat(transactions, hasSize(1));
        assertThat(transactions.get(0).getAmount().toString(), is(transactionAmount));
        assertThat(transactions.get(0).getType(), is(DEPOSIT));
    }
}
