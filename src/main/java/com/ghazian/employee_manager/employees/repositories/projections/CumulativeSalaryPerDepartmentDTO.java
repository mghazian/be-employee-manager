package com.ghazian.employee_manager.employees.repositories.projections;

public interface CumulativeSalaryPerDepartmentDTO {
    String getDepartmentCode();
    Long getNo();
    String getName();
    Long getCumulativeSalary();
}
