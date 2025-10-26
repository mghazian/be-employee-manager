package com.ghazian.employee_manager.employees.repositories.projections;

public interface DepartmentAnalysisByLocationDTO {
    String getLocationName();
    String getDepartmentName();
    Integer getHeadcount();
    Long getSalary();
}
