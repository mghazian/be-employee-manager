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

    @Query(nativeQuery = true, value = """
            select base.department_code, base."no", base."name", (select sum(salary) as cumulative_salary
            	from employees next
            	where base.department_code = next.department_code
            		and next.no < base.no
            		and base.id <> next.id)
            from employees base
            order by base.department_code asc, base."no" asc
            """)
    List<CumulativeSalaryPerDepartmentDTO> getCumulativeSalaryPerDepartment();

    @Query(nativeQuery = true, value = """
            with sorted_entries as (
                select e.id, location_code, l.name as location_name, department_code, d.name as department_name, t.name as tier_name, e."name" , e."no" , e.salary, (
                    select count(*) + 1
                    from employees e2
                    where e2.salary > e.salary
                        and e2.location_code = e.location_code
                        and e2.department_code = e.department_code
                ) as current_rank
                from employees e
                join departments d on d.code = e.department_code\s
                join locations l on l.code = e.location_code\s
                join tiers t on t.code = e.tier_code\s
                order by location_name desc, department_name asc, salary desc
            )
            select location_name, department_name, tier_name, "name", "no", salary, coalesce((
                select next_higher.salary - base.salary
                from sorted_entries next_higher
                where next_higher.salary >= base.salary
                    and next_higher.location_code = base.location_code
                    and next_higher.department_code = base.department_code
                    and base.id <> next_higher.id
                order by next_higher.salary desc
                limit 1
            ), 0) as salary_gap
            from sorted_entries base
            """)
    List<CumulativeSalaryPerDepartmentDTO> getSalaryRankingAndGapAnalysis();


}
