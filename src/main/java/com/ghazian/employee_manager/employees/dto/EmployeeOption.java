package com.ghazian.employee_manager.employees.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeOption {
    Long no;
    String name;
}
