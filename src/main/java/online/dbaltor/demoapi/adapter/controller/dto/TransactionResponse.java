package online.dbaltor.demoapi.adapter.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record TransactionResponse(@NotEmpty String message) {}