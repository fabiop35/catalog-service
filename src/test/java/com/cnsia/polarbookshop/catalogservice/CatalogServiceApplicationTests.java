package com.cnsia.polarbookshop.catalogservice;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

import com.cnsia.polarbookshop.catalogservice.domain.Book;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    private final Logger log = Logger.getLogger(CatalogServiceApplication.class.getName());

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Plorsoiphia");

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value((Book actualBook) -> {
            assertThat(actualBook).isNotNull();
            assertThat(actualBook.isbn())
                    .isEqualTo(expectedBook.isbn());
        });
    }

    @Test
    void whenGetRequestWithIdThenBookReturned() {
        var bookIsbn = "1231231230";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Plorsoiphia");
        Book expectedBook = webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value((Book book) -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        log.info(">>> CatalogServiceApplicationTests.whenGetRequestWithIdThenBookReturned() <<< ");

        webTestClient
                .get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Book.class).value((Book actualBook) -> {
            log.log(Level.INFO, ">@actualBook.isbn: {0}", actualBook.isbn());
            assertThat(actualBook).isNotNull();
            assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
        });
    }

    @Test
    void whenPutRequestThenBookUpdated() {
        log.info(">>> CatalogServiceApplicationTests.whenPutRequestThenBookUpdated() <<< ");
        var bookIsbn = "1231231232";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Palorsophia");
        Book createdBook = webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();
        log.log(Level.INFO, ">CatalogServiceApplicationTests.whenPutRequestThenBookUpdated.publisher: {0}", createdBook.publisher());
        var bookToUpdate = Book.of(createdBook.isbn(), createdBook.title(), createdBook.author(), 7.95, createdBook.publisher());

        webTestClient
                .put()
                .uri("/books/" + bookIsbn)
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value((Book actualBook) -> {
            assertThat(actualBook).isNotNull();
            assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
        });
    }

    @Test
    void whenDeleteRequestThenBookDeleted() {
        var bookIsbn = "1231231233";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
        webTestClient
                .post()
                .uri("/books")
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated();

        webTestClient
                .delete()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient
                .get()
                .uri("/books/" + bookIsbn)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).value(errorMessage
                -> assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
        );
    }

}
