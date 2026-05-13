package org.denic_t.web.dao;

import org.denic_t.web.db.DbUtil;
import org.denic_t.web.entity.ResultEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAOImpl implements ResultDAO {

    public ResultDAOImpl() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS results (" +
                "id SERIAL PRIMARY KEY, " +
                "x DOUBLE PRECISION NOT NULL, " +
                "y DOUBLE PRECISION NOT NULL, " +
                "r DOUBLE PRECISION NOT NULL, " +
                "result BOOLEAN NOT NULL, " +
                "timestamp TIMESTAMP, " +
                "execution_time BIGINT" +
                ")";
        try (Connection conn = DbUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addResult(ResultEntity result) {
        String sql = "INSERT INTO results (x, y, r, result, timestamp, execution_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, result.getX());
            pstmt.setDouble(2, result.getY());
            pstmt.setDouble(3, result.getR());
            pstmt.setBoolean(4, result.isResult());
            pstmt.setTimestamp(5, Timestamp.valueOf(result.getTimestamp()));
            pstmt.setLong(6, result.getExecutionTime());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ResultEntity> getAllResults() {
        List<ResultEntity> results = new ArrayList<>();
        String sql = "SELECT * FROM results ORDER BY id DESC";
        try (Connection conn = DbUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ResultEntity entity = new ResultEntity();
                entity.setId(rs.getLong("id"));
                entity.setX(rs.getDouble("x"));
                entity.setY(rs.getDouble("y"));
                entity.setR(rs.getDouble("r"));
                entity.setResult(rs.getBoolean("result"));
                Timestamp ts = rs.getTimestamp("timestamp");
                if (ts != null) {
                    entity.setTimestamp(ts.toLocalDateTime());
                }
                entity.setExecutionTime(rs.getLong("execution_time"));
                results.add(entity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void clearResults() {
        String sql = "TRUNCATE TABLE results";
        try (Connection conn = DbUtil.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
