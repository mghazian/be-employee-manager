package com.ghazian.employee_manager.employees.repositories.projections;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface CumulativeSalaryPerDepartmentDTO {
    String getDepartmentCode();
    Long getNo();
    String getName();
    Long getCumulativeSalary();
}
