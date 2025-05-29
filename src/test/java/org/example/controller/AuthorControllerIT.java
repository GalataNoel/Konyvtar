package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Author;
import org.example.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        authorRepository.deleteAll();
    }

    @Test
    public void testGetAllAuthors_EmptyDatabase() throws Exception {
        // Given - üres adatbázis

        // When & Then
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAllAuthors_WithData() throws Exception {
        // Given
        Author author1 = new Author();
        author1.setName("J.K. Rowling");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setName("George Orwell");
        authorRepository.save(author2);

        // When & Then
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("J.K. Rowling", "George Orwell")));
    }

    @Test
    public void testGetAuthorById_Found() throws Exception {
        // Given
        Author author = new Author();
        author.setName("Stephen King");
        Author savedAuthor = authorRepository.save(author);

        // When & Then
        mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Stephen King"))
                .andExpect(jsonPath("$.id").value(savedAuthor.getId()));
    }

    @Test
    public void testGetAuthorById_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(get("/api/authors/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateAuthor_Success() throws Exception {
        // Given
        Author newAuthor = new Author();
        newAuthor.setName("Agatha Christie");

        String authorJson = objectMapper.writeValueAsString(newAuthor);

        // When & Then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Agatha Christie"))
                .andExpect(jsonPath("$.id").isNotEmpty());

        // Ellenőrizzük, hogy valóban mentve lett az adatbázisba
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testCreateAuthor_WithEmptyName() throws Exception {
        // Given
        Author newAuthor = new Author();
        newAuthor.setName("");

        String authorJson = objectMapper.writeValueAsString(newAuthor);

        // When & Then - ez lehet hogy 400-at kellene dobnia validáció esetén
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk()); // vagy .andExpect(status().isBadRequest()) validáció esetén
    }

    @Test
    public void testUpdateAuthor_Success() throws Exception {
        // Given - létrehozunk egy szerzőt
        Author existingAuthor = new Author();
        existingAuthor.setName("Isaac Asimov");
        Author savedAuthor = authorRepository.save(existingAuthor);

        // Frissítendő adatok
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Isaac Asimov (Updated)");

        String authorJson = objectMapper.writeValueAsString(updatedAuthor);

        // When & Then
        mockMvc.perform(put("/api/authors/{id}", savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Isaac Asimov (Updated)"))
                .andExpect(jsonPath("$.id").value(savedAuthor.getId()));
    }

    @Test
    public void testUpdateAuthor_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Non-existent Author");

        String authorJson = objectMapper.writeValueAsString(updatedAuthor);

        // When & Then
        mockMvc.perform(put("/api/authors/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAuthor_Success() throws Exception {
        // Given
        Author author = new Author();
        author.setName("Ray Bradbury");
        Author savedAuthor = authorRepository.save(author);

        // When & Then
        mockMvc.perform(delete("/api/authors/{id}", savedAuthor.getId()))
                .andExpect(status().isOk());

        // Ellenőrizzük, hogy valóban törölve lett
        mockMvc.perform(get("/api/authors/{id}", savedAuthor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteAuthor_NotFound() throws Exception {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        mockMvc.perform(delete("/api/authors/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateAuthor_InvalidData() throws Exception {
        // Given - null név esetén
        Author invalidAuthor = new Author();
        // setName() nincs meghívva, tehát null marad

        String authorJson = objectMapper.writeValueAsString(invalidAuthor);

        // When & Then
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk()); // Itt lehet hogy 400-at kellene várni validáció esetén
    }

    @Test
    public void testCompleteWorkflow() throws Exception {
        // Given - Teljes workflow teszt: létrehozás -> lekérés -> frissítés -> törlés

        // 1. Létrehozás
        Author newAuthor = new Author();
        newAuthor.setName("Douglas Adams");

        String authorJson = objectMapper.writeValueAsString(newAuthor);

        String createResponse = mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Author createdAuthor = objectMapper.readValue(createResponse, Author.class);

        // 2. Lekérés ID alapján
        mockMvc.perform(get("/api/authors/{id}", createdAuthor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Douglas Adams"));

        // 3. Frissítés
        Author updatedAuthor = new Author();
        updatedAuthor.setName("Douglas Adams (The Hitchhiker's Guide Author)");

        String updatedJson = objectMapper.writeValueAsString(updatedAuthor);

        mockMvc.perform(put("/api/authors/{id}", createdAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Douglas Adams (The Hitchhiker's Guide Author)"));

        // 4. Törlés
        mockMvc.perform(delete("/api/authors/{id}", createdAuthor.getId()))
                .andExpect(status().isOk());

        // 5. Ellenőrzés, hogy törölve lett
        mockMvc.perform(get("/api/authors/{id}", createdAuthor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateMultipleAuthors() throws Exception {
        // Given - Több szerző létrehozása és ellenőrzése
        String[] authorNames = {"Tolkien", "Martin", "Sanderson", "Jordan"};

        // When - Létrehozzuk az összes szerzőt
        for (String name : authorNames) {
            Author author = new Author();
            author.setName(name);
            String authorJson = objectMapper.writeValueAsString(author);

            mockMvc.perform(post("/api/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(authorJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(name));
        }

        // Then - Ellenőrizzük, hogy mind a 4 szerző létrejött
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(authorNames)));
    }
}