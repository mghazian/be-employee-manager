package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Employee;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeMassWriteRepository {
    void massInsert(List<Employee> input);
}
