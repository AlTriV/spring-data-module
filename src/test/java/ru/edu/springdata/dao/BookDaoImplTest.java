package ru.edu.springdata.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.edu.springdata.model.Book;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class BookDaoImplTest {

    @Autowired
    private BookDaoImpl bookDao;

    @Test
    void find_by_id_with_empty_result() {
        Long id = 1L;
        Optional<Book> book = bookDao.findById(id);

        assertFalse(book.isPresent());
    }

    @Test
    @Sql("/sql/insert_single_book.sql")
    void find_by_id_success() {
        Long id = 1L;
        Optional<Book> book = bookDao.findById(id);

        assertTrue(book.isPresent());
        assertEquals("book1", book.get().getName());
        assertEquals("eng", book.get().getLanguage());
        assertEquals("fantasy", book.get().getCategory());
    }

    @Test
    void find_by_id_with_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> bookDao.findById(null));
    }

    @Test
    void find_all_books_from_empty_table() {
        List<Book> books = bookDao.findALl();

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    @Sql("/sql/insert_books.sql")
    void find_all_books_from_non_empty_table() {
        List<Book> books = bookDao.findALl();

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(3, books.size());
    }

    @Test
    @Sql("/sql/insert_books.sql")
    void find_books_by_language() {
        String language = "eng";

        List<Book> books = bookDao.findAllByLanguage(language);

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(2, books.size());
        assertTrue(books.stream().anyMatch(book -> book.getName().equals("book1")));
    }

    @Test
    @Sql("/sql/insert_books.sql")
    void find_books_by_not_existing_language() {
        String language = "by";

        List<Book> books = bookDao.findAllByLanguage(language);

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    @Sql("/sql/insert_books.sql")
    void find_books_by_category() {
        String category = "non-fiction";

        List<Book> books = bookDao.findAllByCategory(category);

        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        assertEquals("book2", books.get(0).getName());
        assertEquals("eng", books.get(0).getLanguage());
    }

    @Test
    @Sql("/sql/insert_books.sql")
    void find_books_by_not_existing_category() {
        String category = "biography";

        List<Book> books = bookDao.findAllByCategory(category);

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void save_book_success() {
        Book book = Book.builder()
                .name("test_book")
                .language("test_language")
                .category("test_category")
                .build();

        Long id = bookDao.save(book);

        assertNotNull(id);
        assertFalse(bookDao.findALl().isEmpty());
    }

    @Test
    void save_book_with_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> bookDao.save(null));
    }

    @Test
    @Sql("/sql/insert_single_book.sql")
    void update_book_success() {
        String newName = "new_book_name";
        Optional<Book> bookInDB = bookDao.findALl().stream().findFirst();
        assertTrue(bookInDB.isPresent());
        Book book = bookInDB.get();
        Long id = book.getId();
        book.setName(newName);

        boolean isUpdated = bookDao.update(book);

        assertTrue(isUpdated);
        assertEquals(1, bookDao.findALl().size());
        bookInDB = bookDao.findById(id);
        assertTrue(bookInDB.isPresent());
        assertEquals(newName, bookInDB.get().getName());
    }

    @Test
    @Sql("/sql/insert_single_book.sql")
    void update_not_existing_book() {
        Long id = 2L;
        Book book = Book.builder()
                .id(id)
                .name("test_name")
                .language("eng")
                .category("fantasy")
                .build();

        boolean isUpdated = bookDao.update(book);

        assertFalse(isUpdated);
    }

    @Test
    void update_book_with_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> bookDao.update(null));
    }

    @Test
    @Sql("/sql/insert_single_book.sql")
    void delete_book_success() {
        Optional<Book> bookInDB = bookDao.findALl().stream().findFirst();
        assertTrue(bookInDB.isPresent());
        Book book = bookInDB.get();
        Long id = book.getId();

        boolean isDeleted = bookDao.deleteById(id);

        assertTrue(isDeleted);
        assertTrue(bookDao.findALl().isEmpty());
    }

    @Test
    @Sql("/sql/insert_single_book.sql")
    void delete_not_existing_book() {
        Long id = 2L;

        boolean isDeleted = bookDao.deleteById(id);

        assertFalse(isDeleted);
        assertFalse(bookDao.findALl().isEmpty());
    }

    @Test
    void delete_book_with_null_argument() {
        assertThrows(IllegalArgumentException.class, () -> bookDao.deleteById(null));
    }
}