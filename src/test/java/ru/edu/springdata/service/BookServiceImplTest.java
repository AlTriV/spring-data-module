package ru.edu.springdata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edu.springdata.dto.BookDto;
import ru.edu.springdata.entity.Address;
import ru.edu.springdata.entity.Author;
import ru.edu.springdata.entity.Book;
import ru.edu.springdata.entity.Category;
import ru.edu.springdata.exception.BookNotFoundException;
import ru.edu.springdata.exception.IllegalDtoStateException;
import ru.edu.springdata.repository.AuthorRepository;
import ru.edu.springdata.repository.BookRepository;
import ru.edu.springdata.repository.CategoryRepository;
import ru.edu.springdata.service.mapper.BookMapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Spy
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    void find_by_id_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        BookDto expectedDto = bookMapper.toBookDto(book);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<BookDto> resultBookDto = bookServiceImpl.findById(id);

        assertTrue(resultBookDto.isPresent());
        assertEquals(expectedDto, resultBookDto.get());
    }

    @Test
    void find_by_id_fail() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<BookDto> resultBookDto = bookServiceImpl.findById(id);

        assertFalse(resultBookDto.isPresent());
    }

    @Test
    void find_all() {
        Long id = 1L;
        Book book = createBookEntity(id);
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookDto> books = bookServiceImpl.findAll();

        assertFalse(books.isEmpty());
        assertEquals(bookMapper.toBookDto(book), books.get(0));
    }

    @Test
    void find_all_when_result_is_empty() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<BookDto> books = bookServiceImpl.findAll();

        assertTrue(books.isEmpty());
    }

    @Test
    void find_by_category_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        String categoryName = book.getCategory().getName();
        when(bookRepository.findAllByCategory_Name(categoryName)).thenReturn(List.of(book));

        List<BookDto> booksByCategory = bookServiceImpl.findByCategory(categoryName);

        assertFalse(booksByCategory.isEmpty());
        assertEquals(bookMapper.toBookDto(book), booksByCategory.get(0));
    }

    @Test
    void find_by_category_fail() {
        String categoryName = "test";
        when(bookRepository.findAllByCategory_Name(categoryName)).thenReturn(Collections.emptyList());

        List<BookDto> booksByCategory = bookServiceImpl.findByCategory(categoryName);

        assertTrue(booksByCategory.isEmpty());
    }

    @Test
    void find_by_lang_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        String language = book.getLanguage();
        when(bookRepository.findAllByLanguage(language)).thenReturn(List.of(book));

        List<BookDto> booksByLanguage = bookServiceImpl.findByLanguage(language);

        assertFalse(booksByLanguage.isEmpty());
        assertEquals(bookMapper.toBookDto(book), booksByLanguage.get(0));
    }

    @Test
    void find_by_lang_fail() {
        String language = "test";
        when(bookRepository.findAllByLanguage(language)).thenReturn(Collections.emptyList());

        List<BookDto> booksByLanguage = bookServiceImpl.findByLanguage(language);

        assertTrue(booksByLanguage.isEmpty());
    }

    @Test
    void save_book_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        BookDto expectedBookDto = bookMapper.toBookDto(book);
        BookDto bookDto = bookMapper.toBookDto(book);
        bookDto.setId(null);
        String bookCategoryName = book.getCategory().getName();
        when(categoryRepository.findByNameIgnoreCase(bookCategoryName)).thenReturn(Optional.empty());
        when(bookRepository.save(any())).thenReturn(book);

        BookDto savedBook = bookServiceImpl.save(bookDto);

        assertNotNull(savedBook);
        assertEquals(expectedBookDto, savedBook);
        verify(categoryRepository, times(1)).findByNameIgnoreCase(bookCategoryName);
        verify(bookRepository, times(1)).save(any());
        verify(authorRepository, times(1)).findByConcatFirstNameAndLastName(any());
    }

    @Test
    void update_book_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        Book updatedBook = updateBook(book);

        BookDto bookDto = bookMapper.toBookDto(book);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenReturn(updatedBook);

        BookDto updatedBookDto = bookServiceImpl.update(bookDto);

        assertNotNull(updatedBook);
        assertEquals(bookMapper.toBookDto(updatedBook), updatedBookDto);
        verify(bookRepository, times(1)).save(any());
        verify(authorRepository, times(1)).findByConcatFirstNameAndLastName(any());
    }

    @Test
    void update_book_that_not_exists() {
        Long id = 1L;
        Book book = createBookEntity(id);
        BookDto bookDto = bookMapper.toBookDto(book);
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookServiceImpl.update(bookDto));
    }

    @Test
    void update_book_without_id_in_dto() {
        Long id = 1L;
        Book book = createBookEntity(id);
        BookDto bookDto = bookMapper.toBookDto(book);
        bookDto.setId(null);

        assertThrows(IllegalDtoStateException.class, () -> bookServiceImpl.update(bookDto));
    }

    @Test
    void delete_by_id_success() {
        Long id = 1L;
        Book book = createBookEntity(id);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        bookServiceImpl.delete(id);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void delete_by_id_fail() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookServiceImpl.delete(id));
    }

    private Book createBookEntity(Long id) {
        Book book = new Book();
        book.setAuthors(new HashSet<>());
        Set<Book> books = new HashSet<>();
        books.add(book);
        book.setId(id);
        book.setTitle("test_title");
        book.setLanguage("ru");
        book.setCategory(new Category(id, "test_category", books));
        Author author = new Author(id, "first_name", "last_name", "phone", null, books);
        author.setAddress(new Address(id, "city", "street", author));
        book.getAuthors().add(author);
        return book;
    }

    private Book updateBook(Book book) {
        Book updatedBook = new Book();
        book.setId(book.getId());
        updatedBook.setTitle("new_title");
        updatedBook.setLanguage("new_language");

        updatedBook.setCategory(new Category(book.getId() + 1, "new_category", new HashSet<>()));
        updatedBook.getCategory().getBooks().add(updatedBook);

        updatedBook.setAuthors(book.getAuthors());

        return updatedBook;
    }
}