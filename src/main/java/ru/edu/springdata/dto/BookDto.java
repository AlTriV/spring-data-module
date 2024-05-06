package ru.edu.springdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
@NotNull
public class BookDto {

    private Long id;

    @NotBlank(message = "Book title is required")
    private String title;

    private String language;

    @NotBlank(message = "Book category is required")
    private String category;

    private Set<AuthorDto> authors;
}
