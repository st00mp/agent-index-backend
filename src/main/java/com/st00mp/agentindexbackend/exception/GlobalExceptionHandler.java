package com.st00mp.agentindexbackend.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Central error handling for the API.
 * <p>
 * Extends {@link ResponseEntityExceptionHandler} so Spring's standard MVC exceptions
 * (405, 406, 415, malformed body, unknown route, …) keep their correct status codes
 * instead of being swallowed by the {@code Exception.class} catch-all below.
 * <p>
 * Every error body is rendered as JSON with an explicit {@code application/json}
 * content type. Forcing a concrete content type makes Spring skip Accept-based
 * negotiation when writing the body: without it, an endpoint restricted to another
 * media type (e.g. {@code GET /instances/{id}/output}, which is {@code text/plain})
 * collides with these JSON error bodies — a client sending {@code Accept: text/plain}
 * would trigger an {@code HttpMediaTypeNotAcceptableException} while the error is being
 * written, and the intended 4xx would leak out as a bare 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static ResponseEntity<Object> jsonError(HttpStatusCode status, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, headers, status);
    }

    // --- Application-specific exceptions -------------------------------------

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<Object> handleTemplateNotFound(TemplateNotFoundException exception) {
        return jsonError(HttpStatus.NOT_FOUND, Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IncompleteInstanceException.class)
    public ResponseEntity<Object> handleIncompleteInstance(IncompleteInstanceException exception) {
        return jsonError(HttpStatus.BAD_REQUEST, Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(UnresolvedPlaceholderException.class)
    public ResponseEntity<Object> handleUnresolvedPlaceholder(UnresolvedPlaceholderException exception) {
        return jsonError(HttpStatus.UNPROCESSABLE_CONTENT, Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(InstanceNotFoundException.class)
    public ResponseEntity<Object> handleInstanceNotFound(InstanceNotFoundException exception) {
        return jsonError(HttpStatus.NOT_FOUND, Map.of("error", exception.getMessage()));
    }

    // --- Overrides of Spring's standard handling -----------------------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return jsonError(HttpStatus.BAD_REQUEST, errors);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        return jsonError(HttpStatus.BAD_REQUEST, Map.of("error", "Malformed or invalid request body"));
    }

    /**
     * Uniform rendering for every other exception handled by the parent
     * (405, 406, 415, unknown route, …): a JSON {@code {"error": ...}} body with a
     * forced {@code application/json} content type, preserving the resolved status.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        String message = exception.getMessage() != null ? exception.getMessage() : "Request could not be processed";
        return jsonError(statusCode, Map.of("error", message));
    }

    // --- Last-resort catch-all -----------------------------------------------

    /**
     * Turns any otherwise-unmapped exception into a controlled JSON 500 instead of a
     * bare, body-less container error. Standard Spring MVC exceptions are matched by
     * the parent handlers first (they are more specific), so this only catches genuine
     * surprises — it keeps them observable and the error contract uniform.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception exception) {
        return jsonError(HttpStatus.INTERNAL_SERVER_ERROR, Map.of("error", "Internal server error"));
    }
}
