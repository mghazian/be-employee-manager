package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
