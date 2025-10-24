package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Department;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentMassWriteRepository {
    void massInsert(List<Department> input);
}
