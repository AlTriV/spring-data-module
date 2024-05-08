package ru.edu.springdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.edu.springdata.entity.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameIgnoreCase(String name);
}
