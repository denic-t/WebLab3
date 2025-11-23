package org.denic_t.web.db;

import jakarta.enterprise.context.ApplicationScoped;
import org.denic_t.web.CheckResult;

import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ResultDao implements Serializable {

    /**
     * Сохраняет результат проверки в БД.
     * Таблица: CHECK_RESULTS(ID, X, Y, R, HIT, EXECUTION_TIME, CREATED_AT)
     */
    public void save(CheckResult result) throws SQLException {
        String sql = "INSERT INTO CHECK_RESULTS (X, Y, R, HIT, EXECUTION_TIME, CREATED_AT) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, result.getX());
            stmt.setDouble(2, result.getY());
            stmt.setDouble(3, result.getR());
            stmt.setString(4, result.isHit() ? "Y" : "N");
            stmt.setLong(5, result.getExecutionTimeNanos());
            stmt.setTimestamp(6, Timestamp.from(result.getTimestamp()));

            stmt.executeUpdate();
        }
    }

    /**
     * Загружает все результаты из БД, упорядоченные по дате (новые сначала).
     */
    public List<CheckResult> findAll() throws SQLException {
        List<CheckResult> results = new ArrayList<>();
        String sql = "SELECT X, Y, R, HIT, EXECUTION_TIME, CREATED_AT " +
                "FROM CHECK_RESULTS ORDER BY CREATED_AT DESC";

        try (Connection conn = DbUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                double x = rs.getDouble("X");
                double y = rs.getDouble("Y");
                double r = rs.getDouble("R");
                boolean hit = "Y".equals(rs.getString("HIT"));
                long executionTime = rs.getLong("EXECUTION_TIME");
                LocalDateTime timestamp = rs.getTimestamp("CREATED_AT").toLocalDateTime();

                CheckResult result = new CheckResult(x, y, r, hit, executionTime, timestamp);
                results.add(result);
            }
        }

        return results;
    }

    /**
     * Очищает все результаты (для тестирования).
     */
    public void clearAll() throws SQLException {
        String sql = "DELETE FROM CHECK_RESULTS";
        try (Connection conn = DbUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}
