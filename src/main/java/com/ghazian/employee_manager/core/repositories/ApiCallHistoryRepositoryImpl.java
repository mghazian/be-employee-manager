package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.ApiCallHistory;
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
public class ApiCallHistoryRepositoryImpl implements ApiCallHistoryRepository {
    private final DataSource dataSource;

    // TODO: Do not hard code zone id
    ZoneId zoneId = ZoneId.of("GMT+07");
    private final Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone(zoneId));

    @Override
    public ApiCallHistory insert(ApiCallHistory input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO api_call_history (endpoint, http_method, response_status) VALUES (?, ?, ?) RETURNING id
                    """);

            statement.setString(1, input.getEndpoint());
            statement.setString(2, input.getHttpMethod());
            statement.setInt(3, input.getResponseStatus());

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
    public ApiCallHistory update(Long id, ApiCallHistory input) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    UPDATE api_call_history SET endpoint = ?, http_method = ?, response_status = ?, updated_at = ? WHERE id = ?
                    """);

            statement.setString(1, input.getEndpoint());
            statement.setString(2, input.getHttpMethod());
            statement.setInt(3, input.getResponseStatus());
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
                    DELETE FROM api_call_history WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ApiCallHistory toEntity(ResultSet resultSet, ApiCallHistory.ApiCallHistoryBuilder builder) throws SQLException {
        return builder
                .id(resultSet.getLong("id"))
                .endpoint(resultSet.getString("endpoint"))
                .httpMethod(resultSet.getString("http_method"))
                .responseStatus(resultSet.getInt("response_status"))
                .createdAt(resultSet.getTimestamp("created_at", calendar).toLocalDateTime().atZone(zoneId))
                .build();
    }

    @Override
    public Page<ApiCallHistory> findAll(Pageable pageable) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM api_call_history
                    LIMIT ? OFFSET ?
                    """);

            statement.setLong(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            List<ApiCallHistory> output = new ArrayList<>();
            ApiCallHistory.ApiCallHistoryBuilder builder = ApiCallHistory.builder();

            while ( resultSet.next() ) {
                output.add(toEntity(resultSet, builder));
            }

            PreparedStatement countStatement = connection.prepareStatement("SELECT count(*) as cnt FROM api_call_history");
            countStatement.execute();

            ResultSet countResult = countStatement.getResultSet();
            countResult.next();
            long count = countResult.getLong("cnt");

            return new PageImpl<ApiCallHistory>(output, pageable, count);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ApiCallHistory> findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    SELECT * FROM api_call_history
                    WHERE id = ?
                    """);

            statement.setLong(1, id);

            statement.execute();

            ResultSet resultSet = statement.getResultSet();

            if ( !resultSet.next() ) {
                return Optional.empty();
            }

            ApiCallHistory entity = toEntity(resultSet, ApiCallHistory.builder());

            return Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
