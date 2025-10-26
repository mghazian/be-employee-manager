package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Location;
import com.ghazian.employee_manager.core.models.Tier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocationMassWriteRepositoryImpl implements LocationMassWriteRepository {
    private final DataSource dataSource;

    static final int BATCH_VALUE_COUNT_LIMIT = 30000;

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
