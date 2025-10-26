package com.ghazian.employee_manager.employees.repositories;


import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface EmployeeAnalyticRepository extends Repository<Employee, Long> {
    @Query(nativeQuery = true, value= """
            with headcount_per_location_and_department as (
            	select location_code, department_code, count(id) as headcount
            	from employees
            	group by location_code, department_code
            ),
            department_max_headcount_per_location as (
            	select location_code, max(headcount) as headcount
            	from headcount_per_location_and_department
            	group by location_code
            ),
            department_with_max_headcount as (
            	select hplad.*
            	from headcount_per_location_and_department hplad
            	join department_max_headcount_per_location dmhpl on hplad.location_code = dmhpl.location_code and hplad.headcount = dmhpl.headcount
            ),
            average_salary_per_department as (
            	select department_code, avg(salary) as salary
            	from employees
            	group by department_code
            ),
            min_average_salary_per_department as (
            	select min(salary) as salary
            	from average_salary_per_department
            ),
            department_with_min_average_salary as (
            	select department_code
            	from average_salary_per_department aspd
            	join min_average_salary_per_department maspd on aspd.salary = maspd.salary
            ),
            average_salary_of_department_with_min_salary_per_location as (
            	select location_code, avg(salary) as salary
            	from employees e
            	join department_with_min_average_salary dwmas on dwmas.department_code = e.department_code
            	group by location_code
            )
            select l.name as location_name, d.name as department_name, dwmh.headcount, asodwmspl.salary
            from department_with_max_headcount dwmh
            join average_salary_of_department_with_min_salary_per_location asodwmspl on dwmh.location_code = asodwmspl.location_code
            join departments d on dwmh.department_code = d.code
            join locations l on dwmh.location_code = l.code
            """
    )
    List<DepartmentAnalysisByLocationDTO> getDepartmentAnalysisByLocation();
}
