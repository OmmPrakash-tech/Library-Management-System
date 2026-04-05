package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.payload.dto.BookDTO;
import com.example.payload.request.BookSearchRequest;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.BookStatsResponse;
import com.example.payload.response.PageResponse;
import com.example.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Get all books
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {

        List<BookDTO> books = bookService.getAllBooks();

        return ResponseEntity.ok(books);
    }

    // Create a single book
    @PostMapping
    public ResponseEntity<BookDTO> createBook(
            @Valid @RequestBody BookDTO bookDTO) {

        BookDTO createdBook = bookService.createBook(bookDTO);

        return ResponseEntity.ok(createdBook);
    }

    // Bulk create books
    @PostMapping("/bulk")
    public ResponseEntity<List<BookDTO>> createBooksBulk(
            @Valid @RequestBody List<BookDTO> bookDTOs) {

        List<BookDTO> createdBooks = bookService.createBooksBulk(bookDTOs);

        return ResponseEntity.ok(createdBooks);
    }

    // Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {

        BookDTO book = bookService.getBookById(id);

        return ResponseEntity.ok(book);
    }

    // Get book by ISBN
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {

        BookDTO book = bookService.getBookByISBN(isbn);

        return ResponseEntity.ok(book);
    }

    // Update a book
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {

        BookDTO updatedBook = bookService.updateBook(id, bookDTO);

        return ResponseEntity.ok(updatedBook);
    }

    // Soft delete book
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);

        return ResponseEntity.ok(
                new ApiResponse("Book deleted successfully", true));
    }

    // Hard delete book
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse> hardDeleteBook(@PathVariable Long id) {

        bookService.hardDeleteBook(id);

        return ResponseEntity.ok(
                new ApiResponse("Book permanently deleted", true));
    }

    // Advanced search
    @PostMapping("/search")
    public ResponseEntity<PageResponse<BookDTO>> advancedSearch(
            @Valid @RequestBody BookSearchRequest searchRequest) {

        PageResponse<BookDTO> books =
                bookService.searchBooksWithFilters(searchRequest);

        return ResponseEntity.ok(books);
    }

    // Book statistics
   @GetMapping("/stats")
public ResponseEntity<BookStatsResponse> getBookStats() {

    long totalActive = bookService.getTotalActiveBooks();
    long totalAvailable = bookService.getTotalAvailableBooks();
    long totalIssued = bookService.getTotalIssuedBooks();
    long totalOverdue = bookService.getTotalOverdueBooks();

    BookStatsResponse stats = new BookStatsResponse(
            totalActive,
            totalAvailable,
            totalIssued,
            totalOverdue
    );

    return ResponseEntity.ok(stats);
}
}