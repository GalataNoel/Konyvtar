package org.example.controller;

import org.example.model.Author;
import org.example.service.AuthorService;
import org.example.model.Book;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorService.findAuthorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.saveAuthor(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(
            @PathVariable Long id,
            @RequestBody Author authorDetails) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findBooksByAuthorId(id));
    }
}