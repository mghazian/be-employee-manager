package com.ghazian.employee_manager.core.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghazian.employee_manager.core.dto.RestResponse;
import com.ghazian.employee_manager.core.exceptions.ResourceNotFoundException;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    private final ObjectMapper objectMapper;

    public ErrorHandler() {
        objectMapper = new ObjectMapper();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationExceptionHandler(ValidationException ex) {
        try {
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(400))
                    .body(objectMapper.writeValueAsString(Map.of("errors", ex.getErrors())));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(new RestResponse(ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundExceptionHandler(NoHandlerFoundException ex) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(404))
                .body(null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> methodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(405))
                .body(null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestResponse> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(new RestResponse("JSON malformed. Unable to parse JSON"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse> runtimeExceptionHandler(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new RestResponse(ex.getMessage()));
    }
}
