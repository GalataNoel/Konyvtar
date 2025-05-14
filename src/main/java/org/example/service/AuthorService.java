package org.example.service;

import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional(readOnly = true)
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Author> findAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setName(authorDetails.getName());
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Book> findBooksByAuthorId(Long authorId) {
        return authorRepository.findById(authorId)
                .map(Author::getBooks)
                .orElseThrow(() -> new RuntimeException("Author not found"));
    }
}