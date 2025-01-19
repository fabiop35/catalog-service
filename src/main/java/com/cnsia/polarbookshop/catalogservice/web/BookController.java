package com.cnsia.polarbookshop.catalogservice.web;

import java.util.logging.Logger;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cnsia.polarbookshop.catalogservice.domain.Book;
import com.cnsia.polarbookshop.catalogservice.domain.BookService;

@RestController
@RequestMapping("books")
public class BookController {

    private final BookService bookService;
    private static final Logger log = Logger.getLogger(BookController.class.getName());

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Iterable<Book> get() {
        log.info(">>> BookController.get() <<< ");
        return bookService.viewBookList();
    }

    @GetMapping("{isbn}")
    public Book getByIsbn(@PathVariable String isbn) {
        log.info(">>> BookController.getByIsbn() <<< ");
        return bookService.viewBookDetails(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book post(@Valid @RequestBody Book book) {
        log.info(">>> BookController.post() <<< ");
        return bookService.addBookToCatalog(book);
    }

    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        log.info(">>> BookController.delete() <<< ");
        bookService.removeBookFromCatalog(isbn);
    }

    @PutMapping("{isbn}")
    public Book put(@PathVariable String isbn, @Valid @RequestBody Book book) {
        log.info(">>> BookController.put() <<< ");
        return bookService.editBookDetails(isbn, book);
    }
}
