package org.example.controller;

import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateBook() throws Exception {
        String bookJson = """
            {
                "title": "Teszt könyv",
                "isbn": "123-456-789",
                "author": {"id": 1}
            }
            """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk());
    }
}