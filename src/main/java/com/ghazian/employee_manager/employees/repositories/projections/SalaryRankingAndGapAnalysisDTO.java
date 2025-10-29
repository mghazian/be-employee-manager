package com.ghazian.employee_manager.employees.repositories.projections;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SalaryRankingAndGapAnalysisDTO {
    String locationName;
    String departmentName;
    String tierName;
    String name;
    Long salary;
    Long no;
    Long salaryGap;
}
