package online.dbaltor.demoapi.acceptance.steps.apis;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BankingAPI {
    private static final String BASE_URL = "/banking/v1";
    private @NonNull int serverPort;

    public void deposit(String accountNumber, String amount) {
        val transactionRequest = new TransactionRequest(accountNumber, amount);
        testClient()
                .post()
                .uri(BASE_URL + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    public void withdraw(String accountNumber, String amount) {
        val transactionRequest = new TransactionRequest(accountNumber, amount);
        testClient()
                .post()
                .uri(BASE_URL + "/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transactionRequest), TransactionRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    public String getStatement(String accountNumber) {
        return client().get()
                .uri(BASE_URL + "/statement/" + accountNumber)
                .retrieve()
                .bodyToMono(TransactionResponse.class)
                .map(TransactionResponse::message)
                .block();
    }

    private WebTestClient testClient() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + serverPort).build();
    }

    private WebClient client() {
        return WebClient.builder()
                .baseUrl("http://localhost:" + serverPort)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }
}
