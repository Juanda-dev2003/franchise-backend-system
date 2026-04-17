package org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web;

import org.project.tempo.franchisebackendsystem.domain.exception.DomainException;
import org.project.tempo.franchisebackendsystem.domain.exception.EntityNotFoundException;
import org.project.tempo.franchisebackendsystem.infrastructure.adapter.in.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEntityNotFound(EntityNotFoundException exception) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(exception.getMessage())));
    }

    @ExceptionHandler({DomainException.class, IllegalArgumentException.class})
    public Mono<ResponseEntity<ErrorResponse>> handleDomainError(RuntimeException exception) {
        return Mono.just(ResponseEntity.badRequest().body(error(exception.getMessage())));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidRequest(ServerWebInputException exception) {
        return Mono.just(ResponseEntity.badRequest().body(error("Invalid request body")));
    }

    private ErrorResponse error(String message) {
        return new ErrorResponse(message);
    }
}
