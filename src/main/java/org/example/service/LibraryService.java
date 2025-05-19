package org.example.service;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    // Szerző műveletek
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }

    @Transactional
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = getAuthorById(id);
        author.setName(authorDetails.getName());
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        authorRepository.delete(author);
    }

    // Könyv műveletek
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Transactional
    public Book createBook(Book book) {
        // Ellenőrizzük, hogy létezik-e a szerző
        Author author = getAuthorById(book.getAuthor().getId());
        book.setAuthor(author);
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);
        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());

        // Csak akkor frissítjük a szerzőt, ha meg van adva
        if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getId() != null) {
            Author author = getAuthorById(bookDetails.getAuthor().getId());
            book.setAuthor(author);
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }

    // Kapcsolatok kezelése
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthorId(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author not found with id: " + authorId);
        }
        return bookRepository.findByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public Author getAuthorOfBook(Long bookId) {
        Book book = getBookById(bookId);
        return book.getAuthor();
    }
}