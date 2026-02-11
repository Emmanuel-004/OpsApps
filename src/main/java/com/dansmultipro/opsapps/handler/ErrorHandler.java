package com.dansmultipro.opsapps.handler;

import com.dansmultipro.opsapps.dto.ErrorResponseDto;
import com.dansmultipro.opsapps.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        var errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map((ObjectError oe) -> oe.getDefaultMessage())
                .toList();
        return new ResponseEntity<>(new ErrorResponseDto<>(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(RuntimeException ex){
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrationException.class)
    public ResponseEntity<?> handleDataIntegrationException(RuntimeException ex){
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotUniqueException.class)
    public ResponseEntity<?> handleNotUniqueException(RuntimeException ex) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<?> handleNotAllowedException(RuntimeException ex) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<?> handleRateLimitExceededException(RuntimeException ex) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(TokenMismatchException.class)
    public ResponseEntity<?> handleTokenMismatchException(RuntimeException ex) {
        String error = ex.getMessage();
        return new ResponseEntity<>(new ErrorResponseDto<>(error), HttpStatus.BAD_REQUEST);
    }
}
