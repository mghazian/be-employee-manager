package com.ghazian.employee_manager.core.exceptions;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
public class ValidationException extends RuntimeException {
    @Getter @Setter
    Map<String, List<String>> errors;
}
