package com.ghazian.employee_manager.departments.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentDTO {
    private Long id;
    private String name;
    private String code;
}
