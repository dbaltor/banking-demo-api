package online.dbaltor.demoapi.adapter.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import online.dbaltor.demoapi.adapter.controller.dto.TransactionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    // 400 BAD REQUEST - Missing Body
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    TransactionResponse handleHttpMessageNotReadable(HttpServletRequest req, HttpMessageNotReadableException ex) {
        val errorMessage = "Request body is missing";
        log.error("400 BAD REQUEST: " + errorMessage);
        return TransactionResponse.of(errorMessage);
    }

    // 400 BAD REQUEST - Malformed body
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Map<String, String> handleMethodArgumentNotValid(HttpServletRequest req, MethodArgumentNotValidException ex) {
        val errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("")));
        log.error("400 BAD REQUEST: " + errors);
        return errors;
    }

    // 400 BAD REQUEST - Malformed path variable
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    TransactionResponse handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException ex) {
        val errorMessage = ex.getMessage();
        log.error(errorMessage, ex);
        return TransactionResponse.of(errorMessage.substring(errorMessage.indexOf(" ") + 1));
    }
}
