package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.core.repositories.projections.GetEmployeeQueryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeMassWriteRepository {
    @Query(nativeQuery = true,
            countQuery = "select count(id) from employees",
            value = """
            select e.*,
                d.id as department_id, d.name as department_name,
                t.id as tier_id, t.name as tier_name,
                l.id as location_id, l.name as location_name,
                s.id as supervisor_id, s.name as supervisor_name
            from employees e
            join departments d on d.code = e.department_code
            join tiers t on t.code = e.tier_code
            join locations l on l.code = e.location_code
            left join employees s on s.no = e.supervisor_no
            """)
    Page<GetEmployeeQueryDTO> findAllAsCompleteEntries(Pageable pageable);

    @Query(nativeQuery = true,
            value = """
            select e.*,
                d.id as department_id, d.name as department_name,
                t.id as tier_id, t.name as tier_name,
                l.id as location_id, l.name as location_name,
                s.id as supervisor_id, s.name as supervisor_name
            from employees e
            join departments d on d.code = e.department_code
            join tiers t on t.code = e.tier_code
            join locations l on l.code = e.location_code
            left join employees s on s.no = e.supervisor_no
            where e.id = :id
            """)
    Optional<GetEmployeeQueryDTO> findOneAsCompleteEntry(@Param("id") Long id);
}
