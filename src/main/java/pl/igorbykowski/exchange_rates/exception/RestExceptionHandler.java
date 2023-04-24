package pl.igorbykowski.exchange_rates.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String LOG_ERROR_EXCEPTION_OCCURRED_MSG = "An exception occurred, which will cause a '{}' response.";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("message", ((HttpStatus) status).getReasonPhrase());

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        body.put("errors", errors);
        body.put("instance", ((ServletWebRequest) request).getRequest().getRequestURI());
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             @Nullable Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        if (statusCode.is5xxServerError()) {
            log.error(LOG_ERROR_EXCEPTION_OCCURRED_MSG, statusCode);
        } else if (statusCode.is4xxClientError()) {
            log.warn(LOG_ERROR_EXCEPTION_OCCURRED_MSG, statusCode);
        } else {
            log.debug(LOG_ERROR_EXCEPTION_OCCURRED_MSG, statusCode);
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn(LOG_ERROR_EXCEPTION_OCCURRED_MSG, exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<Object> handleNotFound(HttpClientErrorException ex, WebRequest request) {
        HttpStatusCode status = ex.getStatusCode();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("title", status.toString());
        if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) body.put("message", "Resource Not Found");
        else body.put("message", ex.getMessage());
        body.put("instance", ((ServletWebRequest) request).getRequest().getRequestURI());
        return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
    }
}
