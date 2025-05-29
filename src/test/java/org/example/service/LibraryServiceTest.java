package org.example.service;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Author;
import org.example.model.Book;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LibraryService libraryService;

    private Author testAuthor;
    private Book testBook;

    @BeforeEach
    void setUp() {
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setName("Test Author");

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("123-456-789");
        testBook.setAuthor(testAuthor);
    }

    // ========== Szerző műveletek tesztjei ==========

    @Test
    void getAllAuthors_ShouldReturnAllAuthors() {
        // Given
        List<Author> authors = Arrays.asList(testAuthor);
        when(authorRepository.findAll()).thenReturn(authors);

        // When
        List<Author> result = libraryService.getAllAuthors();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Author", result.get(0).getName());
        verify(authorRepository).findAll();
    }

    @Test
    void getAuthorById_ShouldReturnAuthor_WhenExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));

        // When
        Author result = libraryService.getAuthorById(1L);

        // Then
        assertEquals("Test Author", result.getName());
        verify(authorRepository).findById(1L);
    }

    @Test
    void getAuthorById_ShouldThrowException_WhenNotExists() {
        // Given
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.getAuthorById(999L)
        );
        assertEquals("Author not found with id: 999", exception.getMessage());
    }

    @Test
    void createAuthor_ShouldReturnSavedAuthor() {
        // Given
        when(authorRepository.save(testAuthor)).thenReturn(testAuthor);

        // When
        Author result = libraryService.createAuthor(testAuthor);

        // Then
        assertEquals("Test Author", result.getName());
        verify(authorRepository).save(testAuthor);
    }

    @Test
    void updateAuthor_ShouldUpdateAndReturnAuthor_WhenExists() {
        // Given
        Author updateDetails = new Author();
        updateDetails.setName("Updated Name");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);

        // When
        Author result = libraryService.updateAuthor(1L, updateDetails);

        // Then
        assertEquals("Updated Name", testAuthor.getName());
        verify(authorRepository).save(testAuthor);
    }

    @Test
    void updateAuthor_ShouldThrowException_WhenNotExists() {
        // Given
        Author updateDetails = new Author();
        updateDetails.setName("Updated Name");

        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.updateAuthor(999L, updateDetails)
        );
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthor_WhenExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));

        // When
        libraryService.deleteAuthor(1L);

        // Then
        verify(authorRepository).delete(testAuthor);
    }

    @Test
    void deleteAuthor_ShouldThrowException_WhenNotExists() {
        // Given
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.deleteAuthor(999L)
        );
    }

    // ========== Könyv műveletek tesztjei ==========

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        // When
        List<Book> result = libraryService.getAllBooks();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository).findAll();
    }

    @Test
    void getBookById_ShouldReturnBook_WhenExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        Book result = libraryService.getBookById(1L);

        // Then
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getBookById_ShouldThrowException_WhenNotExists() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.getBookById(999L)
        );
        assertEquals("Book not found with id: 999", exception.getMessage());
    }

    @Test
    void createBook_ShouldReturnSavedBook_WhenAuthorExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(bookRepository.save(testBook)).thenReturn(testBook);

        // When
        Book result = libraryService.createBook(testBook);

        // Then
        assertEquals("Test Book", result.getTitle());
        assertEquals(testAuthor, result.getAuthor());
        verify(bookRepository).save(testBook);
    }

    @Test
    void createBook_ShouldThrowException_WhenAuthorNotExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.createBook(testBook)
        );
    }

    @Test
    void updateBook_ShouldUpdateWithAuthor_WhenBookAndAuthorExist() {
        // Given
        Author newAuthor = new Author();
        newAuthor.setId(2L);
        newAuthor.setName("New Author");

        Book updateDetails = new Book();
        updateDetails.setTitle("Updated Title");
        updateDetails.setIsbn("987-654-321");
        updateDetails.setAuthor(newAuthor);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = libraryService.updateBook(1L, updateDetails);

        // Then
        assertEquals("Updated Title", testBook.getTitle());
        assertEquals("987-654-321", testBook.getIsbn());
        assertEquals(newAuthor, testBook.getAuthor());
        verify(bookRepository).save(testBook);
    }

    @Test
    void updateBook_ShouldUpdateWithoutAuthor_WhenAuthorIsNull() {
        // Given
        Book updateDetails = new Book();
        updateDetails.setTitle("Updated Title");
        updateDetails.setIsbn("987-654-321");
        updateDetails.setAuthor(null); // Nincs szerző

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = libraryService.updateBook(1L, updateDetails);

        // Then
        assertEquals("Updated Title", testBook.getTitle());
        assertEquals("987-654-321", testBook.getIsbn());
        assertEquals(testAuthor, testBook.getAuthor()); // Eredeti szerző marad
        verify(bookRepository).save(testBook);
        verify(authorRepository, never()).findById(any()); // Nem hívja a szerző keresést
    }

    @Test
    void updateBook_ShouldUpdateWithoutAuthor_WhenAuthorIdIsNull() {
        // Given
        Author authorWithNullId = new Author();
        authorWithNullId.setId(null);

        Book updateDetails = new Book();
        updateDetails.setTitle("Updated Title");
        updateDetails.setIsbn("987-654-321");
        updateDetails.setAuthor(authorWithNullId);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // When
        Book result = libraryService.updateBook(1L, updateDetails);

        // Then
        assertEquals("Updated Title", testBook.getTitle());
        assertEquals(testAuthor, testBook.getAuthor()); // Eredeti szerző marad
        verify(authorRepository, never()).findById(any());
    }

    @Test
    void updateBook_ShouldThrowException_WhenBookNotExists() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.updateBook(999L, testBook)
        );
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        libraryService.deleteBook(1L);

        // Then
        verify(bookRepository).delete(testBook);
    }

    @Test
    void deleteBook_ShouldThrowException_WhenNotExists() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.deleteBook(999L)
        );
    }

    // ========== Kapcsolatok kezelése tesztjei ==========

    @Test
    void getBooksByAuthorId_ShouldReturnBooks_WhenAuthorExists() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(authorRepository.existsById(1L)).thenReturn(true);
        when(bookRepository.findByAuthorId(1L)).thenReturn(books);

        // When
        List<Book> result = libraryService.getBooksByAuthorId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookRepository).findByAuthorId(1L);
    }

    @Test
    void getBooksByAuthorId_ShouldThrowException_WhenAuthorNotExists() {
        // Given
        when(authorRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.getBooksByAuthorId(999L)
        );
        assertEquals("Author not found with id: 999", exception.getMessage());
    }

    @Test
    void getAuthorOfBook_ShouldReturnAuthor_WhenBookExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // When
        Author result = libraryService.getAuthorOfBook(1L);

        // Then
        assertEquals(testAuthor, result);
        assertEquals("Test Author", result.getName());
    }

    @Test
    void getAuthorOfBook_ShouldThrowException_WhenBookNotExists() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> libraryService.getAuthorOfBook(999L)
        );
    }
}