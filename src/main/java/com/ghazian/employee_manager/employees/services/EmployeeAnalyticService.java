package com.ghazian.employee_manager.employees.services;

import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import com.ghazian.employee_manager.employees.repositories.projections.SalaryRankingAndGapAnalysisDTO;

import java.util.List;

public interface EmployeeAnalyticService {
    List<DepartmentAnalysisByLocationDTO> getDepartmentAnalysisByLocation();
    List<CumulativeSalaryPerDepartmentDTO> getCumulativeSalaryPerDepartment();
    List<SalaryRankingAndGapAnalysisDTO> getSalaryRankingAndGapAnalysis();
}
