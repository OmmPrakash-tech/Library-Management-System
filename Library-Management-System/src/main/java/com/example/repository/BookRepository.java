package com.example.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

   @Query("""
SELECT b FROM Book b 
WHERE 
(:searchTerm IS NULL OR :searchTerm = '' OR
 LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
 LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
 LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
 
AND (:genreId IS NULL OR b.genre.id = :genreId)

AND (:availableOnly IS NULL OR 
     (:availableOnly = true AND b.availableCopies > 0) OR 
     (:availableOnly = false))

AND b.active = true
""")
Page<Book> searchBooksWithFilters(
        @Param("searchTerm") String searchTerm,
        @Param("genreId") Long genreId,
        @Param("availableOnly") Boolean availableOnly,
        Pageable pageable
);

    long countByActiveTrue();

    @Query("SELECT COUNT(b) FROM Book b WHERE b.availableCopies > 0 AND b.active = true")
    long countAvailableBooks();

    long countByGenreIdAndActiveTrue(Long genreId);
}