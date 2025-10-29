package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Location;
import com.ghazian.employee_manager.core.models.Location;
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
public class LocationRepositoryImpl implements LocationRepository {
    private final DataSource dataSource;

    static final int BATCH_VALUE_COUNT_LIMIT = 30000;

    // TODO: Do not hard code zone id
    ZoneId zoneId = ZoneId.of("GMT+07");
    private final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));

    @Override
    public Location insert(Location input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO locations (code, name, address) VALUES (?, ?, ?) RETURNING id
                    """);

            statement.setString(1, input.getCode());
            statement.setString(2, input.getName());
            statement.setString(3, input.getAddress());

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
    public Location update(Long id, Location input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE locations SET code = ?, name = ?, address = ?, updated_at = ? WHERE id = ?
                    """);

            statement.setString(1, input.getCode());
            statement.setString(2, input.getName());
            statement.setString(3, input.getAddress());
            statement.setTimestamp(4, Timestamp.from(ZonedDateTime.now(zoneId).toInstant()));
            statement.setLong(5, id);

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
                    DELETE FROM locations WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Location toEntity(ResultSet resultSet, Location.LocationBuilder builder) throws SQLException {
        return builder
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .code(resultSet.getString("code"))
                .address(resultSet.getString("address"))
                .createdAt(resultSet.getTimestamp("created_at", calendar).toLocalDateTime().atZone(zoneId))
                .createdAt(resultSet.getTimestamp("updated_at", calendar).toLocalDateTime().atZone(zoneId))
                .build();
    }

    @Override
    public Page<Location> findAll(Pageable pageable) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM locations
                    LIMIT ? OFFSET ?
                    """);

            statement.setLong(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<Location> output = new ArrayList<>();
            Location.LocationBuilder builder = Location.builder();

            while ( resultSet.next() ) {
                output.add(toEntity(resultSet, builder));
            }

            PreparedStatement countStatement = connection.prepareStatement("SELECT count(*) as cnt FROM locations");
            countStatement.execute();

            ResultSet countResult = countStatement.getResultSet();
            countResult.next();
            long count = countResult.getLong("cnt");

            return new PageImpl<Location>(output, pageable, count);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Location> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM locations
                    WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if ( !resultSet.next() ) {
                return Optional.empty();
            }

            Location entity = toEntity(resultSet, Location.builder());

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void massInsert(List<Location> input) {
        String sql = "INSERT INTO locations (code, name, address) VALUES :valuePlaceholder";
        final int COLUMN_NUMBER = 3;
        final int MAX_ROW_PER_BATCH = BATCH_VALUE_COUNT_LIMIT / COLUMN_NUMBER;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            for (int currentIndex = 0; currentIndex < input.size(); currentIndex += MAX_ROW_PER_BATCH) {
                StringBuilder valuePlaceholderBuilder = new StringBuilder();
                for (int i = 0; i + currentIndex < input.size(); i++) {
                    valuePlaceholderBuilder.append("(?, ?, ?), ");
                }

                // Remove the last comma
                final String valuePlaceholder = valuePlaceholderBuilder.substring(0, valuePlaceholderBuilder.length() - 2);

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql.replace(":valuePlaceholder", valuePlaceholder))) {
                    List<Location> currentBatchValues = input.subList(currentIndex, Math.min(input.size(), currentIndex + MAX_ROW_PER_BATCH));

                    int placeholderIndex = 1; // 1-based
                    for (Location data : currentBatchValues) {
                        preparedStatement.setString(placeholderIndex++, data.getCode());
                        preparedStatement.setString(placeholderIndex++, data.getName());
                        preparedStatement.setString(placeholderIndex++, data.getAddress());
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
