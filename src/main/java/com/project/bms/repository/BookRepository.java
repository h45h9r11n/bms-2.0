package com.project.bms.repository;

import com.project.bms.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Book findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title = ?";
        try {
            jdbcTemplate.queryForObject(sql, new BookRowMapper(), title);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new BookRowMapper(), title);
    }

    public void save(Book book) {
        String sql = "INSERT INTO books (title, author, price, description, image) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPrice());
            ps.setString(4, book.getDescription());
            ps.setString(5, book.getImage());
            return ps;
        });
    }

    public Book findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try {
            jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return null;
        }
        return jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        return jdbcTemplate.query(sql, new BookRowMapper());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(rs.getString("author"));
            book.setPrice(rs.getString("price"));
            book.setDescription(rs.getString("description"));
            book.setImage(rs.getString("image"));
            return book;
        }
    }
}
