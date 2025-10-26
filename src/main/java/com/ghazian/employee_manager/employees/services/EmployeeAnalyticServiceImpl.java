package com.ghazian.employee_manager.employees.services;

import com.ghazian.employee_manager.employees.repositories.EmployeeAnalyticRepository;
import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeAnalyticServiceImpl implements EmployeeAnalyticService {

    private final EmployeeAnalyticRepository employeeAnalyticRepository;


    @Override
    public List<DepartmentAnalysisByLocationDTO> getDepartmentAnalysisByLocation() {
        return employeeAnalyticRepository.getDepartmentAnalysisByLocation();
    }

    @Override
    public List<CumulativeSalaryPerDepartmentDTO> getCumulativeSalaryPerDepartment() {
        return employeeAnalyticRepository.getCumulativeSalaryPerDepartment();
    }

    @Override
    public List<CumulativeSalaryPerDepartmentDTO> getSalaryRankingAndGapAnalysis() {
        return employeeAnalyticRepository.getSalaryRankingAndGapAnalysis();
    }
}
