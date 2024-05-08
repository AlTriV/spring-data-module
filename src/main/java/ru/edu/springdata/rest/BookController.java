package ru.edu.springdata.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.edu.springdata.dto.BookDto;
import ru.edu.springdata.service.BookService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> findAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findBookById(@NotNull @PathVariable Long id) {
        Optional<BookDto> bookDto = bookService.findById(id);
        return bookDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BookDto>> findBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.findByCategory(category));
    }

    @GetMapping("/lang/{lang}")
    public ResponseEntity<List<BookDto>> findBooksByLanguage(@PathVariable String lang) {
        return ResponseEntity.ok(bookService.findByLanguage(lang));
    }

    @PostMapping("/create")
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.save(bookDto));
    }

    @PostMapping("/update")
    public ResponseEntity<BookDto> updateBook(@Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.update(bookDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BookDto> deleteBook(@NotNull @PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.ok().build();
    }
}
