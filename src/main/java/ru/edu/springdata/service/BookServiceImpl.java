package ru.edu.springdata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.edu.springdata.dto.AddressDto;
import ru.edu.springdata.dto.AuthorDto;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public Optional<BookDto> findById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toBookDto);
    }

    @Override
    @Transactional
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(bookMapper::toBookDto).toList();
    }

    @Override
    @Transactional
    public List<BookDto> findByLanguage(String language) {
        return bookRepository.findAllByLanguage(language).stream().map(bookMapper::toBookDto).toList();
    }

    @Override
    @Transactional
    public List<BookDto> findByCategory(String category) {
        return bookRepository.findAllByCategory_Name(category).stream().map(bookMapper::toBookDto).toList();
    }

    @Override
    @Transactional
    public BookDto save(BookDto bookDto) {
        Book bookEntity = createBookEntity(bookDto);

        String categoryName = bookDto.getCategory();
        Category category = categoryRepository.findByNameIgnoreCase(categoryName).orElse(createCategoryEntity(categoryName));
        bookEntity.setCategory(category);

        Set<Author> authors = getAuthorsForBook(bookDto, false);
        bookEntity.setAuthors(authors);

        Book savedBook = bookRepository.save(bookEntity);
        return bookMapper.toBookDto(savedBook);
    }

    @Override
    @Transactional
    public BookDto update(BookDto bookDto) {
        if (bookDto.getId() == null) {
            throw new IllegalDtoStateException("BookDto id should not be null for update");
        }
        Book bookEntity = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id %s not found", bookDto.getId())));
        bookEntity.setTitle(bookDto.getTitle());
        bookEntity.setLanguage(bookDto.getLanguage());

        String categoryName = bookDto.getCategory();
        String categoryEntityName = bookEntity.getCategory().getName();
        if (!categoryEntityName.equals(categoryName)) {
            Category newCategory = createCategoryEntity(categoryName);
            bookEntity.setCategory(newCategory);
        }

        Set<Author> authors = getAuthorsForBook(bookDto, true);
        bookEntity.getAuthors().forEach(author -> author.getBooks().remove(bookEntity));
        bookEntity.getAuthors().clear();
        bookEntity.getAuthors().addAll(authors);

        Book savedBook = bookRepository.save(bookEntity);
        return bookMapper.toBookDto(savedBook);
    }

    @Override
    @Transactional
    public void delete(Long bookId) {
        Book bookEntity = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book with id %s not found", bookId)));
        bookEntity.getAuthors().clear();
        bookEntity.setCategory(null);
        bookRepository.delete(bookEntity);
    }

    @Transactional
    protected Set<Author> getAuthorsForBook(BookDto bookDto, boolean updateAuthorFields) {
        Map<String, AuthorDto> authorDtoMap = bookDto.getAuthors().stream().collect(Collectors.toMap(
                dto -> dto.getFirstName().concat(dto.getLastName()).toUpperCase(),
                dto -> dto
        ));

        List<Author> authorsInDB = authorRepository.findByConcatFirstNameAndLastName(authorDtoMap.keySet());
        Map<String, Author> authorMap = authorsInDB.stream().collect(Collectors.toMap(
                author -> author.getFirstName().concat(author.getLastName()).toUpperCase(),
                author -> author
        ));

        for (String authorName : authorDtoMap.keySet()) {
            AuthorDto authorDto = authorDtoMap.get(authorName);
            if (!authorMap.containsKey(authorName)) {
                Author newAuthorEntity = createAuthorEntity(authorDto);
                authorMap.put(authorName, newAuthorEntity);
            } else if (updateAuthorFields) {
                Author author = authorMap.get(authorName);
                author.setPhone(authorDto.getPhone());
                author.getAddress().setCity(authorDto.getAddress().getCity());
                author.getAddress().setStreet(authorDto.getAddress().getStreet());
            }
        }

        return new HashSet<>(authorMap.values());
    }

    private Book createBookEntity(BookDto bookDto) {
        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setLanguage(bookDto.getLanguage());
        return book;
    }

    private Category createCategoryEntity(String categoryName) {
        return new Category(null, categoryName, new HashSet<>());
    }

    private Author createAuthorEntity(AuthorDto authorDto) {
        Author author = new Author();
        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author.setPhone(authorDto.getPhone());
        AddressDto addressDto = authorDto.getAddress();
        author.setAddress(new Address(null, addressDto.getCity(), addressDto.getStreet(), author));
        author.setBooks(new HashSet<>());
        return author;
    }
}
