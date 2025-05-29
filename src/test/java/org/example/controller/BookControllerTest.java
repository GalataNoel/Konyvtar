package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Book;
import org.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllBooks_ShouldReturnBooks() throws Exception {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.findAllBooks()).thenReturn(Arrays.asList(book));

        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() throws Exception {
        // Given
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.findBookById(1L)).thenReturn(Optional.of(book));

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_ShouldReturn404_WhenNotExists() throws Exception {
        // Given
        when(bookService.findBookById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }
}