package com.ghazian.employee_manager.employees.repositories.projections;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public interface SalaryRankingAndGapAnalysisDTO {
    String getLocationName();
    String getDepartmentName();
    String getTierName();
    String getName();
    Long getSalary();
    Long getNo();
    Long getSalaryGap();
}
