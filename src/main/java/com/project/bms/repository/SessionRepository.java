package com.project.bms.repository;

import com.project.bms.model.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class SessionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Session session) {
        String sql = "INSERT INTO sessions (id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, session.getSessionId());
            ps.setLong(2, session.getUserId());
            return ps;
        });
    }

    public Session findBySessionId(String sessionId) {
        String sql = "SELECT * FROM sessions WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, new SessionRowMapper(), sessionId);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new SessionRowMapper(), sessionId);
    }

    public void delete(String sessionId) {
        String sql = "DELETE FROM sessions WHERE id = ?";
        jdbcTemplate.update(sql, sessionId);
    }

    private static class SessionRowMapper implements RowMapper<Session>{
        @Override
        public Session mapRow(ResultSet resultSet, int i) {
            Session session = new Session();
            try {
                session.setSessionId(resultSet.getString("id"));
                session.setUserId(resultSet.getLong("user_id"));
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            return session;
        }
    }
}
