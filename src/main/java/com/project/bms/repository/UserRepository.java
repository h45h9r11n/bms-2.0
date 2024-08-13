package com.project.bms.repository;

import com.project.bms.model.Book;
import com.project.bms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";
        try{
            jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), username);

    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, password, email, fullname, role, avatar) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getAvatar());
            return ps;
        });
    }

    public void update(User user){
        String sql = "UPDATE users SET username = ?, email = ?, fullname = ?, role = ?, avatar = ? WHERE id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getFullname());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getAvatar());
            ps.setLong(6, user.getId());
            return ps;
        });
    }

    public void updatePassword(Long id, String password){
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, password);
            ps.setLong(2, id);
            return ps;
        });
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, new UserRepository.UserRowMapper(), id);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new UserRepository.UserRowMapper(), id);
    }
    public void delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setFullname(rs.getString("fullname"));
            user.setAvatar(rs.getString("avatar"));
            user.setRole(rs.getString("role"));
            return user;
        }
    }
}
