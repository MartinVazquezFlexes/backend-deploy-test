package com.techforb.apiportalrecruiting.core.exceptions;

import com.techforb.apiportalrecruiting.core.dtos.ApiError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleError(Exception exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleError(MethodArgumentNotValidException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleError(ResponseStatusException exception) {
        ApiError apiError = buildError(exception.getReason(), HttpStatus.valueOf(exception.getStatusCode().value()));
        return ResponseEntity.status(exception.getStatusCode()).body(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleError(EntityNotFoundException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleError(IllegalArgumentException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(VacancyNotActiveException.class)
    public ResponseEntity<ApiError> handleError(VacancyNotActiveException exception){
        ApiError apiError= buildError(exception.getMessage(),HttpStatus.GONE);
        return ResponseEntity.status(HttpStatus.GONE).body(apiError);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ApiError> handleError(UnauthorizedActionException exception){
        ApiError apiError= buildError(exception.getMessage(),HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(CvNotOwnedException.class)
    public ResponseEntity<ApiError> handleError(CvNotOwnedException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(AlreadyAssignedCvException.class)
    public ResponseEntity<ApiError> handleError(AlreadyAssignedCvException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(ApplicationClosedException.class)
    public ResponseEntity<ApiError> handleError(ApplicationClosedException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(SavedVacancyNotFoundException.class)
    public ResponseEntity<ApiError> handleError(SavedVacancyNotFoundException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(SavedVacancyAlreadySavedException.class)
    public ResponseEntity<ApiError> handleError(SavedVacancyAlreadySavedException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(SavedVacancyInactiveException.class)
    public ResponseEntity<ApiError> handleError(SavedVacancyInactiveException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(SavedVacancyAuthenticationException.class)
    public ResponseEntity<ApiError> handleError(SavedVacancyAuthenticationException exception) {
        ApiError apiError = buildError(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleError(MethodArgumentTypeMismatchException exception) {
        ApiError apiError = buildError("Invalid parameter format: " + exception.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    private ApiError buildError(String message, HttpStatus status) {
        return ApiError.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(status.getReasonPhrase())
                .status(status.value())
                .message(message)
                .build();
    }

}
