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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // 400 BAD REQUEST - Missing Body
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        val errorMessage = "Request body is missing";
        log.error("400 BAD REQUEST: " + errorMessage);
        return this.handleExceptionInternal(ex, errorMessage, headers, status, request);
    }

    // 400 BAD REQUEST - Malformed body
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        val errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .collect(toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage));

        log.error("400 BAD REQUEST: " + errors);
        return handleExceptionInternal(ex, errors, headers, BAD_REQUEST, request);
    }

    // 400 BAD REQUEST - Malformed path variable
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    TransactionResponse ConstraintViolationException(HttpServletRequest req, ConstraintViolationException ex) {
        val errorMessage = ex.getMessage();
        log.error(errorMessage, ex);
        return TransactionResponse.of(errorMessage.substring(errorMessage.indexOf(" ") + 1));
    }
}
