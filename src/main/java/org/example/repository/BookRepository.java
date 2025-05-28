package org.example.repository;

import org.example.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Könyv keresése cím alapján
    List<Book> findByTitle(String title);

    // Könyv keresése cím alapján (részleges egyezés, kis/nagybetű független)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Könyv keresése ISBN alapján
    Optional<Book> findByIsbn(String isbn);

    // Könyvek keresése szerző alapján
    List<Book> findByAuthorId(Long authorId);

    // Könyvek keresése szerző neve alapján
    List<Book> findByAuthorName(String authorName);

    // Könyvek keresése szerző neve alapján (részleges egyezés)
    List<Book> findByAuthorNameContainingIgnoreCase(String authorName);

    // Összes könyv egy adott szerzőtől (custom query)
    @Query("SELECT b FROM Book b WHERE b.author.name = :authorName")
    List<Book> findBooksByAuthorName(@Param("authorName") String authorName);

    // Könyvek keresése cím vagy szerző neve alapján
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> findByTitleOrAuthorNameContaining(@Param("keyword") String keyword);

    // Ellenőrzi, hogy létezik-e könyv az adott ISBN-nel
    boolean existsByIsbn(String isbn);

    // Megszámolja egy szerző könyveinek számát
    long countByAuthorId(Long authorId);
}