package org.example.config;

import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initDatabase(AuthorRepository authorRepo, BookRepository bookRepo) {
        return args -> {
            Author author1 = new Author();
            author1.setName("J.K. Rowling");
            authorRepo.save(author1);

            Book book1 = new Book();
            book1.setTitle("Harry Potter és a bölcsek köve");
            book1.setIsbn("963-8386-87-0");
            book1.setAuthor(author1);
            bookRepo.save(book1);

            Author author2 = new Author();
            author2.setName("Stephen King");
            authorRepo.save(author2);
        };
    }
}