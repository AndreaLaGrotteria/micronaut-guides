/*
 * Library
 * This is a library API
 *
 * The version of the OpenAPI document: 1.0.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

// tag::header[]
package example.micronaut.controller;
//tag::import[]

import example.micronaut.BookEntity;
import example.micronaut.BookRepository;
import example.micronaut.api.BooksApi;
import example.micronaut.model.BookInfo;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import java.util.List;
//end::import[]

@Controller
public class BooksController implements BooksApi {
//end::header[]

    //tag::inject[]
    private final BookRepository bookRepository; // <1>

    public BooksController(BookRepository bookRepository) { // <1>
        this.bookRepository = bookRepository;
    }
    //end::inject[]

    //tag::addBook[]
    @ExecuteOn(TaskExecutors.BLOCKING)
    public void addBook(BookInfo bookInfo) {
        bookRepository.save(bookInfo.getName(), // <3>
                bookInfo.getAvailability(),
                bookInfo.getAuthor(),
                bookInfo.getISBN());
    }
    //end::addBook[]


    //tag::search[]
    @ExecuteOn(TaskExecutors.BLOCKING) // <1>
    public List<BookInfo> search(
            String bookName,
            String authorName) {
        return searchEntities(bookName, authorName)
                .stream()
                .map(this::map) // <5>
                .toList();
    }

    private BookInfo map(BookEntity entity) {
        var book = new BookInfo(entity.name(), entity.availability());
        book.setISBN(entity.isbn());
        book.setAuthor(entity.author());
        return book;
    }

    @NonNull
    private List<BookEntity> searchEntities(String name, String author) { // <2>
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(author)) {
            return bookRepository.findAll();
        } else if (StringUtils.isEmpty(name)) {
            return bookRepository.findAllByAuthorContains(author); // <3>

        } else  if (StringUtils.isEmpty(author)) {
            return bookRepository.findAllByNameContains(name);
        } else {
            return bookRepository.findAllByAuthorContainsAndNameContains(author,name); // <4>
        }
    }
    //end::search[]
//tag::footer[]
}
//end::footer[]
