package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.core.repositories.base.MassWriteRepository;
import com.ghazian.employee_manager.core.repositories.base.RawQueryRepository;
import com.ghazian.employee_manager.core.repositories.projections.GetEmployeeQueryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends RawQueryRepository<Employee, Long>, MassWriteRepository<Employee> {
    Page<GetEmployeeQueryDTO> findAllAsCompleteEntries(Pageable pageable);
    Optional<GetEmployeeQueryDTO> findOneAsCompleteEntry(@Param("id") Long id);
}
