package ru.edu.springdata.dao;

import ru.edu.springdata.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    Optional<Book> findById(Long id);

    List<Book> findALl();

    List<Book> findAllByLanguage(String language);

    List<Book> findAllByCategory(String category);

    Long save(Book book);

    boolean update(Book book);

    boolean deleteById(Long id);
}
