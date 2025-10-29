package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Department;
import com.ghazian.employee_manager.core.models.Department;
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
public class DepartmentRepositoryImpl implements DepartmentRepository {
    private final DataSource dataSource;

    static final int BATCH_VALUE_COUNT_LIMIT = 30000;

    // TODO: Do not hard code zone id
    ZoneId zoneId = ZoneId.of("GMT+07");
    private final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));

    @Override
    public Department insert(Department input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO departments (code, name) VALUES (?, ?) RETURNING id
                    """);

            statement.setString(1, input.getCode());
            statement.setString(2, input.getName());

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
    public Department update(Long id, Department input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE departments SET code = ?, name = ?, updated_at = ? WHERE id = ?
                    """);

            statement.setString(1, input.getCode());
            statement.setString(2, input.getName());
            statement.setTimestamp(3, Timestamp.from(ZonedDateTime.now(zoneId).toInstant()));
            statement.setLong(4, id);

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
                    DELETE FROM departments WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Department toEntity(ResultSet resultSet, Department.DepartmentBuilder builder) throws SQLException {
        return builder
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .code(resultSet.getString("code"))
                .createdAt(resultSet.getTimestamp("created_at", calendar).toLocalDateTime().atZone(zoneId))
                .createdAt(resultSet.getTimestamp("updated_at", calendar).toLocalDateTime().atZone(zoneId))
                .build();
    }

    @Override
    public Page<Department> findAll(Pageable pageable) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM departments
                    LIMIT ? OFFSET ?
                    """);

            statement.setLong(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<Department> output = new ArrayList<>();
            Department.DepartmentBuilder builder = Department.builder();

            while ( resultSet.next() ) {
                output.add(toEntity(resultSet, builder));
            }

            PreparedStatement countStatement = connection.prepareStatement("SELECT count(*) as cnt FROM departments");
            countStatement.execute();

            ResultSet countResult = countStatement.getResultSet();
            countResult.next();
            long count = countResult.getLong("cnt");

            return new PageImpl<Department>(output, pageable, count);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Department> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM departments
                    WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if ( !resultSet.next() ) {
                return Optional.empty();
            }

            Department entity = toEntity(resultSet, Department.builder());

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void massInsert(List<Department> input) {
        String sql = "INSERT INTO departments (code, name) VALUES :valuePlaceholder";
        final int COLUMN_NUMBER = 2;
        final int MAX_ROW_PER_BATCH = BATCH_VALUE_COUNT_LIMIT / COLUMN_NUMBER;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (int currentIndex = 0; currentIndex < input.size(); currentIndex += MAX_ROW_PER_BATCH) {
                StringBuilder valuePlaceholderBuilder = new StringBuilder();
                for (int i = 0; i + currentIndex < input.size(); i++) {
                    valuePlaceholderBuilder.append("(?, ?), ");
                }

                // Remove the last comma
                final String valuePlaceholder = valuePlaceholderBuilder.substring(0, valuePlaceholderBuilder.length() - 2);

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.replace(":valuePlaceholder", valuePlaceholder))) {
                    List<Department> currentBatchValues = input.subList(currentIndex, Math.min(input.size(), currentIndex + MAX_ROW_PER_BATCH));

                    int placeholderIndex = 1; // 1-based
                    for (Department department : currentBatchValues) {
                        preparedStatement.setString(placeholderIndex++, department.getCode());
                        preparedStatement.setString(placeholderIndex++, department.getName());
                    }

                    preparedStatement.execute();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
