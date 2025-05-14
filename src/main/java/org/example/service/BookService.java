package org.example.service;

import org.example.model.Book;
import org.example.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}