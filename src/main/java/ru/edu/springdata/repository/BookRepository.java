package ru.edu.springdata.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import ru.edu.springdata.entity.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"authors", "authors.address", "category"})
    @Override
    Optional<Book> findById(@NotNull Long id);

    @EntityGraph(attributePaths = {"authors", "authors.address", "category"})
    List<Book> findAll();

    @EntityGraph(attributePaths = {"authors", "authors.address", "category"})
    List<Book> findAllByCategory_Name(String categoryName);

    @EntityGraph(attributePaths = {"authors", "authors.address", "category"})
    List<Book> findAllByLanguage(String language);
}
