package com.ghazian.employee_manager.employees.repositories.projections;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CumulativeSalaryPerDepartmentDTO {
    String departmentCode;
    Long no;
    String name;
    Long cumulativeSalary;
}
