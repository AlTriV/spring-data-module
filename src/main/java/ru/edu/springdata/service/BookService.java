package ru.edu.springdata.service;

import ru.edu.springdata.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Optional<BookDto> findById(Long id);

    List<BookDto> findAll();

    List<BookDto> findByLanguage(String language);

    List<BookDto> findByCategory(String category);

    BookDto save(BookDto bookDto);

    BookDto update(BookDto bookDto);

    void delete(Long bookId);
}
