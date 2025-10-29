package com.ghazian.employee_manager.employees.repositories;

import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import com.ghazian.employee_manager.employees.repositories.projections.SalaryRankingAndGapAnalysisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class EmployeeAnalyticRepositoryImpl implements EmployeeAnalyticRepository {
    private final DataSource dataSource;

    // TODO: Do not hard code zone id
    ZoneId zoneId = ZoneId.of("GMT+07");
    private final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));

    @Override
    public List<DepartmentAnalysisByLocationDTO> getDepartmentAnalysisByLocation() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
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
                select l.name as locationName, d.name as departmentName, dwmh.headcount, asodwmspl.salary
                from department_with_max_headcount dwmh
                join average_salary_of_department_with_min_salary_per_location asodwmspl on dwmh.location_code = asodwmspl.location_code
                join departments d on dwmh.department_code = d.code
                join locations l on dwmh.location_code = l.code
                """);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<DepartmentAnalysisByLocationDTO> output = new ArrayList<>();
            DepartmentAnalysisByLocationDTO.DepartmentAnalysisByLocationDTOBuilder builder = DepartmentAnalysisByLocationDTO.builder();

            while ( resultSet.next() ) {
                output.add(builder
                                .departmentName(resultSet.getString("departmentName"))
                                .locationName(resultSet.getString("locationName"))
                                .headcount(resultSet.getInt("headcount"))
                                .salary(resultSet.getLong("salary"))
                        .build()
                );
            }

            return output;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CumulativeSalaryPerDepartmentDTO> getCumulativeSalaryPerDepartment() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    select base.department_code as departmentCode, base.no, base.name, (select sum(salary) as cumulativeSalary
                        from employees next
                        where base.department_code = next.department_code
                            and next.no < base.no
                            and base.id <> next.id)
                    from employees base
                    order by base.department_code asc, base."no" asc
                    """);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<CumulativeSalaryPerDepartmentDTO> output = new ArrayList<>();
            CumulativeSalaryPerDepartmentDTO.CumulativeSalaryPerDepartmentDTOBuilder builder = CumulativeSalaryPerDepartmentDTO.builder();

            while ( resultSet.next() ) {
                output.add(builder
                                .departmentCode(resultSet.getString("departmentCode"))
                                .no(resultSet.getLong("no"))
                                .name(resultSet.getString("name"))
                                .cumulativeSalary(resultSet.getLong("cumulativeSalary"))
                        .build());
            }

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SalaryRankingAndGapAnalysisDTO> getSalaryRankingAndGapAnalysis() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
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
                    select location_name as locationName, department_name as departmentName, tier_name as tierName, base.name, base.no, salary, coalesce((
                        select next_higher.salary - base.salary
                        from sorted_entries next_higher
                        where next_higher.salary >= base.salary
                            and next_higher.location_code = base.location_code
                            and next_higher.department_code = base.department_code
                            and base.id <> next_higher.id
                        order by next_higher.salary desc
                        limit 1
                    ), 0) as salaryGap
                    from sorted_entries base
                    """);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<SalaryRankingAndGapAnalysisDTO> output = new ArrayList<>();
            SalaryRankingAndGapAnalysisDTO.SalaryRankingAndGapAnalysisDTOBuilder builder = SalaryRankingAndGapAnalysisDTO.builder();

            while ( resultSet.next() ) {
                output.add(builder
                                .locationName(resultSet.getString("locationName"))
                                .departmentName(resultSet.getString("departmentName"))
                                .tierName(resultSet.getString("tierName"))
                                .name(resultSet.getString("name"))
                                .no(resultSet.getLong("no"))
                                .salary(resultSet.getLong("salary"))
                                .salaryGap(resultSet.getLong("salaryGap"))
                        .build());
            }

            return output;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
