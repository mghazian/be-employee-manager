package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Department;
import com.ghazian.employee_manager.core.repositories.base.MassWriteRepository;
import com.ghazian.employee_manager.core.repositories.base.RawQueryRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends RawQueryRepository<Department, Long>, MassWriteRepository<Department> {
}
