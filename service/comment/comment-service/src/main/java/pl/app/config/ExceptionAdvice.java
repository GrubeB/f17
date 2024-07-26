package pl.app.config;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import pl.app.common.shared.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> authenticationExceptionHandler(AuthenticationException exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(apiError);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiError> authorizationExceptionHandler(AuthorizationException exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(apiError);
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiError> invalidStateExceptionHandler(InvalidStateException exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiError> iOExceptionHandler(IOException exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFoundExceptionHandler(NotFoundException exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> validationExceptionHandler(Exception exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exceptionHandler(Exception exception, ServerWebExchange exchange) {
        logger.error(exception.getMessage(), exception);
        ApiError apiError = new ApiError(
                exchange.getRequest().getURI().getPath(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
