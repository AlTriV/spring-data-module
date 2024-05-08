package ru.edu.springdata.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.edu.springdata.dao.BookDao;
import ru.edu.springdata.model.Book;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/books")
public class BookController {

    private BookDao bookDao;

    @Autowired
    public BookController(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookDao.findById(id);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookDao.findALl());
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<Book>> getBooksByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(bookDao.findAllByLanguage(language));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookDao.findAllByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Long> createBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookDao.save(book));
    }

    @PostMapping("/update")
    public ResponseEntity<Long> updateBook(@RequestBody Book book) {
        if (bookDao.update(book)) {
            return ResponseEntity.ok(book.getId());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteBook(@PathVariable Long id) {
        if (bookDao.deleteById(id)) {
            return ResponseEntity.ok(id);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
