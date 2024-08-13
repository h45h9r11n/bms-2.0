package com.project.bms.repository;

import com.project.bms.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Comment findById(Long id){
        String sql = "SELECT * FROM comments WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, new CommentRowMapper(), id);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new CommentRowMapper(), id);
    }

    public List<Comment> findByBookId(Long bookId){
        String sql = "SELECT * FROM comments WHERE book_id = ?";
        try {
            jdbcTemplate.query(sql, new CommentRowMapper(), bookId);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.query(sql, new CommentRowMapper(), bookId);
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setBookid(rs.getLong("book_id"));
            comment.setUserid(rs.getLong("user_id"));
            comment.setContent(rs.getString("content"));
            return comment;
        }

    }
}
