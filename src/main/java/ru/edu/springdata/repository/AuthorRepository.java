package ru.edu.springdata.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import ru.edu.springdata.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @EntityGraph(attributePaths = {"address"})
    @Query("select a from Author a where concat(upper(a.firstName), upper(a.lastName) ) in :concatNames")
    List<Author> findByConcatFirstNameAndLastName(Set<String> concatNames);
}
