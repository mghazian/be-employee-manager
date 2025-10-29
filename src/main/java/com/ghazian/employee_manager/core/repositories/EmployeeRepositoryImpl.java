package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.core.repositories.projections.GetEmployeeQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final DataSource dataSource;

    static final int BATCH_VALUE_COUNT_LIMIT = 30000;

    // TODO: Do not hard code zone id
    ZoneId zoneId = ZoneId.of("GMT+07");
    private final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));

    @Override
    public Employee insert(Employee input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO employees (no, name, tier_code, department_code, location_code, supervisor_no, salary, entry_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
                    """);

            statement.setLong(1, input.getNo());
            statement.setString(2, input.getName());
            statement.setLong(3, input.getTierCode());
            statement.setString(4, input.getDepartmentCode());
            statement.setString(5, input.getLocationCode());
            if ( null == input.getSupervisorNo() ) { statement.setNull(6, Types.BIGINT); }
            else { statement.setLong(6, input.getSupervisorNo()); }
            statement.setLong(7, input.getSalary());
            statement.setTimestamp(8, Timestamp.from(input.getEntryDate().toInstant()), calendar);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();
            resultSet.next();

            long id = resultSet.getLong("id");
            input.setId(id);

            return input;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Employee update(Long id, Employee input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE employees
                    SET no = ?,
                        name = ?,
                        tier_code = ?,
                        department_code = ?,
                        location_code = ?,
                        supervisor_no = ?,
                        salary = ?,
                        entry_date = ?,
                        updated_at = ?
                    WHERE id = ?
                    """);

            statement.setLong(1, input.getNo());
            statement.setString(2, input.getName());
            statement.setLong(3, input.getTierCode());
            statement.setString(4, input.getDepartmentCode());
            statement.setString(5, input.getLocationCode());
            if ( null == input.getSupervisorNo() ) { statement.setNull(6, Types.BIGINT); }
            else { statement.setLong(6, input.getSupervisorNo()); }
            statement.setLong(7, input.getSalary());
            statement.setTimestamp(8, Timestamp.from(input.getEntryDate().toInstant()), calendar);

            statement.setTimestamp(9, Timestamp.from(ZonedDateTime.now(zoneId).toInstant()));
            statement.setLong(10, id);

            statement.execute();

            return input;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    DELETE FROM employees WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Employee toEntity(ResultSet resultSet, Employee.EmployeeBuilder builder) throws SQLException {
        return builder
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .no(resultSet.getLong("no"))
                .departmentCode(resultSet.getString("department_code"))
                .locationCode(resultSet.getString("location_code"))
                .tierCode(resultSet.getLong("tier_code"))
                .supervisorNo(resultSet.getLong("supervisor_no"))
                .salary(resultSet.getLong("salary"))
                .entryDate(resultSet.getTimestamp("entry_date", calendar).toLocalDateTime().atZone(zoneId))
                .createdAt(resultSet.getTimestamp("created_at", calendar).toLocalDateTime().atZone(zoneId))
                .createdAt(resultSet.getTimestamp("updated_at", calendar).toLocalDateTime().atZone(zoneId))
                .build();
    }

    private GetEmployeeQueryDTO toEntity(ResultSet resultSet, GetEmployeeQueryDTO.GetEmployeeQueryDTOBuilder builder) throws SQLException {
        return builder
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .no(resultSet.getLong("no"))
                .departmentId(resultSet.getLong("department_id"))
                .departmentCode(resultSet.getString("department_code"))
                .departmentName(resultSet.getString("department_name"))
                .locationId(resultSet.getLong("location_id"))
                .locationCode(resultSet.getString("location_code"))
                .locationName(resultSet.getString("location_name"))
                .tierId(resultSet.getLong("tier_id"))
                .tierCode(resultSet.getLong("tier_code"))
                .tierName(resultSet.getString("tier_name"))
                .supervisorId(resultSet.getLong("supervisor_id"))
                .supervisorNo(resultSet.getLong("supervisor_no"))
                .supervisorName(resultSet.getString("supervisor_name"))
                .salary(resultSet.getLong("salary"))
                .entryDate(resultSet.getTimestamp("entry_date", calendar).toInstant())
                .createdAt(resultSet.getTimestamp("created_at", calendar).toInstant())
                .createdAt(resultSet.getTimestamp("updated_at", calendar).toInstant())
                .build();
    }

    @Override
    public Page<Employee> findAll(Pageable pageable) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM employees
                    LIMIT ? OFFSET ?
                    """);

            if ( pageable == null ) {
                statement.setNull(1, Types.BIGINT);
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(1, pageable.getPageSize());
                statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());
            }

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<Employee> output = new ArrayList<>();
            Employee.EmployeeBuilder builder = Employee.builder();

            while ( resultSet.next() ) {
                output.add(toEntity(resultSet, builder));
            }

            PreparedStatement countStatement = connection.prepareStatement("SELECT count(*) as cnt FROM employees");
            countStatement.execute();

            ResultSet countResult = countStatement.getResultSet();
            countResult.next();
            long count = countResult.getLong("cnt");

            return new PageImpl<Employee>(output, pageable == null ? Pageable.unpaged() : pageable, count);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Employee> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM employees
                    WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if ( !resultSet.next() ) {
                return Optional.empty();
            }

            Employee entity = toEntity(resultSet, Employee.builder());

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void massInsert(List<Employee> input) {
        String sql = "INSERT INTO employees (no, name, tier_code, department_code, location_code, supervisor_no, salary, entry_date) VALUES :valuePlaceholder";
        final int COLUMN_NUMBER = 8;
        final int MAX_ROW_PER_BATCH = BATCH_VALUE_COUNT_LIMIT / COLUMN_NUMBER;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (int currentIndex = 0; currentIndex < input.size(); currentIndex += MAX_ROW_PER_BATCH) {
                StringBuilder valuePlaceholderBuilder = new StringBuilder();
                for (int i = 0; i + currentIndex < input.size(); i++) {
                    valuePlaceholderBuilder.append("(?, ?, ?, ?, ?, ?, ?, ?), ");
                }

                // Remove the last comma
                final String valuePlaceholder = valuePlaceholderBuilder.substring(0, valuePlaceholderBuilder.length() - 2);

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.replace(":valuePlaceholder", valuePlaceholder))) {
                    List<Employee> currentBatchValues = input.subList(currentIndex, Math.min(input.size(), currentIndex + MAX_ROW_PER_BATCH));

                    int placeholderIndex = 1; // 1-based
                    for (Employee data : currentBatchValues) {
                        preparedStatement.setLong(placeholderIndex++, data.getNo());
                        preparedStatement.setString(placeholderIndex++, data.getName());
                        preparedStatement.setLong(placeholderIndex++, data.getTierCode());
                        preparedStatement.setString(placeholderIndex++, data.getDepartmentCode());
                        preparedStatement.setString(placeholderIndex++, data.getLocationCode());

                        if ( null == data.getSupervisorNo() ) { preparedStatement.setNull(placeholderIndex++, Types.BIGINT); }
                        else { preparedStatement.setLong(placeholderIndex++, data.getSupervisorNo()); }

                        preparedStatement.setLong(placeholderIndex++, data.getSalary());
                        preparedStatement.setTimestamp(placeholderIndex++, java.sql.Timestamp.valueOf(data.getEntryDate().toLocalDateTime()));
                    }

                    preparedStatement.execute();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<GetEmployeeQueryDTO> findAllAsCompleteEntries(Pageable pageable) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
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
                    limit ? offset ?
                    """);

            statement.setLong(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            statement.execute();

            List<GetEmployeeQueryDTO> output = new ArrayList<>();
            GetEmployeeQueryDTO.GetEmployeeQueryDTOBuilder builder = GetEmployeeQueryDTO.builder();

            ResultSet resultSet = statement.getResultSet();
            while ( resultSet.next() ) {
                output.add(toEntity(resultSet, builder));
            }

            PreparedStatement countStatement = connection.prepareStatement("""
                    select count(*) as cnt
                    from employees e
                    join departments d on d.code = e.department_code
                    join tiers t on t.code = e.tier_code
                    join locations l on l.code = e.location_code
                    left join employees s on s.no = e.supervisor_no
                    """);

            countStatement.execute();

            ResultSet countResult = countStatement.getResultSet();
            countResult.next();

            long count = countResult.getLong("cnt");

            return new PageImpl<>(output, pageable, count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<GetEmployeeQueryDTO> findOneAsCompleteEntry(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
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
                    where e.id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if ( !resultSet.next() ) {
                return Optional.empty();
            }

            GetEmployeeQueryDTO entity = toEntity(resultSet, GetEmployeeQueryDTO.builder());

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
