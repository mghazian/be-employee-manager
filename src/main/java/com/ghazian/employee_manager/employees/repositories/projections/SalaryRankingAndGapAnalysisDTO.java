package com.ghazian.employee_manager.employees.repositories.projections;

public interface SalaryRankingAndGapAnalysisDTO {
    String getLocationName();
    String getDepartmentName();
    String getTierName();
    String getName();
    Long getSalary();
    Long getNo();
    Long getSalaryGap();
}
