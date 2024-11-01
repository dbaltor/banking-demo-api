package online.dbaltor.demoapi.adapter.controller;

import static online.dbaltor.demoapi.application.AccountException.ErrorType.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionRequest;
import online.dbaltor.demoapi.application.AccountException;
import online.dbaltor.demoapi.application.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BankingController.class)
public class BankingControllerTest {
    private static final String BASE_URL = "/banking/v1";
    @Autowired ObjectMapper objectMapper;
    @Autowired private MockMvc mockMvc;

    @MockBean private AccountService accountService;

    @Test
    void shouldReturn200WhenDepositBodyIsValid() throws Exception {
        // Given
        val transactionRequest = new TransactionRequest("123456", "100.00");
        // When
        mockMvc.perform(
                        post(BASE_URL + "/deposit")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(transactionRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Deposit transaction successful")));
    }

    @Test
    void shouldReturn200WhenWithdrawalBodyIsValid() throws Exception {
        // Given
        val transactionRequest = new TransactionRequest("123456", "100.00");
        // When
        mockMvc.perform(
                        post(BASE_URL + "/withdrawal")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(transactionRequest)))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Withdrawal transaction successful")));
    }

    @Test
    void shouldReturn409WhenInsufficientFunds() throws Exception {
        // Given
        doThrow(AccountException.of(INSUFFICIENT_FUNDS, Optional.empty()))
                .when(accountService)
                .withdraw(any(), any());
        val transactionRequest = new TransactionRequest("123456", "100.00");
        // When
        mockMvc.perform(
                        post(BASE_URL + "/withdrawal")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(transactionRequest)))
                // Then
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Insufficient funds")));
    }

    @Test
    void shouldReturn400WhenDepositBodyIsMissing() throws Exception {
        // When
        mockMvc.perform(post(BASE_URL + "/deposit").contentType(APPLICATION_JSON_VALUE))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Request body is missing")));
    }

    @Test
    void shouldReturn400WhenWithdrawalAccountIsNotValid() throws Exception {
        // Given
        val transactionRequest = new TransactionRequest("xxx", "100.00");
        // When
        mockMvc.perform(
                        post(BASE_URL + "/withdrawal")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(transactionRequest)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.account", is("Account number must be a 6-digit number")));
    }

    @Test
    void shouldReturn400WhenWithdrawalAmountIsNotValid() throws Exception {
        // Given
        val transactionRequest = new TransactionRequest("123456", "xxx");
        // When
        mockMvc.perform(
                        post(BASE_URL + "/withdrawal")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(transactionRequest)))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(
                        jsonPath(
                                "$.amount",
                                is("Amount can be up to 99999.99 with pence being optional")));
    }

    @Test
    void shouldReturn404WhenAccountIsNotInformed() throws Exception {
        // When
        mockMvc.perform(get(BASE_URL + "/statement/").contentType(APPLICATION_JSON_VALUE))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Resource not found")));
    }

    @Test
    void shouldReturn400WhenStatementAccountIsNotValid() throws Exception {
        // When
        mockMvc.perform(get(BASE_URL + "/statement/xxx").contentType(APPLICATION_JSON_VALUE))
                // Then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Account number must be a 6-digit number")));
    }

    @Test
    void shouldReturn404WhenStatementAccountIsNotFound() throws Exception {
        // Given
        val accountDbException = AccountException.of(ACCOUNT_NOT_FOUND, Optional.empty());
        given(accountService.getStatement(any())).willThrow(accountDbException);
        // When
        mockMvc.perform(get(BASE_URL + "/statement/123456").contentType(APPLICATION_JSON_VALUE))
                // Then
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Account not found")));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorHappens() throws Exception {
        // Given
        val accountDbException = AccountException.of(UNEXPECTED, Optional.empty());
        given(accountService.getStatement(any())).willThrow(accountDbException);
        // When
        mockMvc.perform(get(BASE_URL + "/statement/123456").contentType(APPLICATION_JSON_VALUE))
                // Then
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Something went wrong")));
    }
}
