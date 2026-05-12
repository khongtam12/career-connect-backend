package iuh.fit.companyservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), resolveMessage(fieldError));
        }

        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            if ("Founded year must not be greater than the current year".equals(objectError.getDefaultMessage())) {
                errors.putIfAbsent("foundedYear", objectError.getDefaultMessage());
            }
        }

        return ResponseEntity.badRequest().body(buildBody("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> errors.putIfAbsent(
                violation.getPropertyPath().toString(),
                violation.getMessage()
        ));

        return ResponseEntity.badRequest().body(buildBody("Validation failed", errors));
    }

    private Map<String, Object> buildBody(String message, Map<String, String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", message);
        body.put("errors", errors);
        return body;
    }

    private String resolveMessage(FieldError fieldError) {
        return fieldError.getDefaultMessage();
    }
}
