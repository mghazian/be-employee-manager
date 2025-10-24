package com.ghazian.employee_manager.core.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
                    .body(objectMapper.writeValueAsString(ex.getErrors()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
