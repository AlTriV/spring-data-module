package ru.edu.springdata.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.edu.springdata.dao.mapper.BookRowMapper;
import ru.edu.springdata.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

@Repository
public class BookDaoImpl implements BookDao {

    private static final String SELECT_BY_ID = "SELECT * FROM books WHERE id = :id";
    private static final String SELECT_ALL = "SELECT * FROM books";
    private static final String SELECT_BY_LANGUAGE = "SELECT * FROM books WHERE language = :language";
    private static final String SELECT_BY_CATEGORY = "SELECT * FROM books WHERE category = :category";
    private static final String UPDATE_BY_ID = "UPDATE books SET name = :name, language = :language, category = :category WHERE id = :id";
    private static final String DELETE_BY_ID = "DELETE FROM books WHERE id = :id";


    private final SimpleJdbcInsert insertIntoBooks;
    private final NamedParameterJdbcTemplate parameterTemplate;
    private final BookRowMapper bookRowMapper;

    @Autowired
    public BookDaoImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate parameterTemplate, BookRowMapper bookRowMapper) {
        this.insertIntoBooks = new SimpleJdbcInsert(jdbcTemplate).withTableName("books").usingGeneratedKeyColumns("id");
        this.parameterTemplate = parameterTemplate;
        this.bookRowMapper = bookRowMapper;
    }

    @Override
    public Optional<Book> findById(Long id) {
        if (isNull(id)) {
            throw new IllegalArgumentException("Database delete. Param id cannot be null");
        }
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        List<Book> books = parameterTemplate.query(SELECT_BY_ID, parameterSource, bookRowMapper);
        return books.stream().findFirst();
    }

    @Override
    public List<Book> findALl() {
        return parameterTemplate.query(SELECT_ALL, bookRowMapper);
    }

    @Override
    public List<Book> findAllByLanguage(String language) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("language", language);
        return parameterTemplate.query(SELECT_BY_LANGUAGE, parameterSource, bookRowMapper);
    }

    @Override
    public List<Book> findAllByCategory(String category) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("category", category);
        return parameterTemplate.query(SELECT_BY_CATEGORY, parameterSource, bookRowMapper);
    }

    @Override
    public Long save(Book book) {
        if (isNull(book)) {
            throw new IllegalArgumentException("Database save. Param book cannot be null");
        }
        SqlParameterSource parameterSource = new MapSqlParameterSource(Map.of(
                "name", book.getName(),
                "language", book.getLanguage(),
                "category", book.getCategory()
        ));
        return insertIntoBooks.executeAndReturnKey(parameterSource).longValue();
    }

    @Override
    public boolean update(Book book) {
        if (isNull(book)) {
            throw new IllegalArgumentException("Database update. Param book cannot be null");
        }
        SqlParameterSource parameterSource = new MapSqlParameterSource(Map.of(
                "id", book.getId(),
                "name", book.getName(),
                "language", book.getLanguage(),
                "category", book.getCategory()
        ));
        return parameterTemplate.update(UPDATE_BY_ID, parameterSource) == 1;
    }

    @Override
    public boolean deleteById(Long id) {
        if (isNull(id)) {
            throw new IllegalArgumentException("Database delete. Param id cannot be null");
        }
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        return parameterTemplate.update(DELETE_BY_ID, parameterSource) == 1;
    }
}
