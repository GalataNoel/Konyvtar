package org.example.config;

import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;

    public DataLoader(AuthorRepository authorRepo, BookRepository bookRepo) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
    }

    @Override
    public void run(String... args) {
        // Először töröljük a meglévő adatokat a tiszta kezdéshez
        bookRepo.deleteAll();
        authorRepo.deleteAll();

        try {
            // J.K. Rowling és könyve
            Author rowling = new Author();
            rowling.setName("J.K. Rowling");
            rowling = authorRepo.save(rowling);

            Book harryPotter = new Book();
            harryPotter.setTitle("Harry Potter és a bölcsek köve");
            harryPotter.setIsbn("963-8386-87-0");
            harryPotter.setAuthor(rowling);
            bookRepo.save(harryPotter);

            // Stephen King
            Author king = new Author();
            king.setName("Stephen King");
            authorRepo.save(king);

            System.out.println("Adatbázis inicializálása sikeres!");
        } catch (Exception e) {
            System.err.println("Hiba az adatbázis inicializálása során: " + e.getMessage());
            throw e;
        }
    }
}