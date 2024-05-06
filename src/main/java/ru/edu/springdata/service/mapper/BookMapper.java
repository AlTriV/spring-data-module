package ru.edu.springdata.service.mapper;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import ru.edu.springdata.dto.AddressDto;
import ru.edu.springdata.dto.AuthorDto;
import ru.edu.springdata.dto.BookDto;
import ru.edu.springdata.entity.Address;
import ru.edu.springdata.entity.Author;
import ru.edu.springdata.entity.Book;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class BookMapper {

    public BookDto toBookDto(@NotNull Book book) {
        BookDto bookDto = new BookDto();

        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setLanguage(book.getLanguage());
        bookDto.setCategory(book.getCategory().getName());

        if (!isNull(book.getAuthors()) && !book.getAuthors().isEmpty()) {
            Set<AuthorDto> authors = book.getAuthors().stream()
                    .map(this::toAuthorDto)
                    .collect(Collectors.toSet());
            bookDto.setAuthors(authors);
        }

        return bookDto;
    }

    private AuthorDto toAuthorDto(Author author) {
        AuthorDto authorDto = new AuthorDto();

        authorDto.setFirstName(author.getFirstName());
        authorDto.setLastName(author.getLastName());
        authorDto.setPhone(author.getPhone());

        if (!isNull(author.getAddress())) {
            Address address = author.getAddress();
            AddressDto addressDto = new AddressDto();
            addressDto.setCity(address.getCity());
            addressDto.setStreet(address.getStreet());
            authorDto.setAddress(addressDto);
        }

        return authorDto;
    }
}
