package com.ghazian.employee_manager.employees.repositories;


import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import com.ghazian.employee_manager.employees.repositories.projections.SalaryRankingAndGapAnalysisDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface EmployeeAnalyticRepository {
    List<DepartmentAnalysisByLocationDTO> getDepartmentAnalysisByLocation();
    List<CumulativeSalaryPerDepartmentDTO> getCumulativeSalaryPerDepartment();
    List<SalaryRankingAndGapAnalysisDTO> getSalaryRankingAndGapAnalysis();


}
