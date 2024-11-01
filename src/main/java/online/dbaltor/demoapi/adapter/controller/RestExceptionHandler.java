package online.dbaltor.demoapi.adapter.controller;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    TransactionResponse handle(HttpServletRequest req, NoResourceFoundException ex) {
        val errorMessage = "Resource not found";
        log.error("404 NOT FOUND: " + ex.getMessage());
        return new TransactionResponse(errorMessage);
    }

    // 400 BAD REQUEST - Missing Body
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    TransactionResponse handleHttpMessageNotReadable(
            HttpServletRequest req, HttpMessageNotReadableException ex) {
        val errorMessage = "Request body is missing";
        log.error("400 BAD REQUEST: " + ex.getMessage());
        return new TransactionResponse(errorMessage);
    }

    // 400 BAD REQUEST - Malformed body
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Map<String, String> handleMethodArgumentNotValid(
            HttpServletRequest req, MethodArgumentNotValidException ex) {
        val errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .collect(
                                toMap(
                                        FieldError::getField,
                                        fieldError ->
                                                Optional.ofNullable(fieldError.getDefaultMessage())
                                                        .orElse("")));
        log.error("400 BAD REQUEST: " + errors);
        return errors;
    }

    // 400 BAD REQUEST - Malformed path variable
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    TransactionResponse handleConstraintViolation(
            HttpServletRequest req, ConstraintViolationException ex) {
        val errorMessage = ex.getMessage();
        log.error(errorMessage, ex);
        return new TransactionResponse(errorMessage.substring(errorMessage.indexOf(" ") + 1));
    }
}
