package iuh.fit.userservice.exception;

import iuh.fit.userservice.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // keep default
        }

        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }
}
