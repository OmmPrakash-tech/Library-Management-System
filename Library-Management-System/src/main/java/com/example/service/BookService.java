package com.example.service;

import java.util.List;

import com.example.payload.dto.BookDTO;
import com.example.payload.request.BookSearchRequest;
import com.example.payload.response.PageResponse;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    List<BookDTO> createBooksBulk(List<BookDTO> bookDTOs);

    BookDTO getBookById(Long bookId);

    List<BookDTO> getAllBooks();

    BookDTO getBookByISBN(String isbn);

    BookDTO updateBook(Long bookId, BookDTO bookDTO);

    void deleteBook(Long bookId);      // Soft delete

    void hardDeleteBook(Long bookId);  // Permanent delete

    PageResponse<BookDTO> searchBooksWithFilters(BookSearchRequest searchRequest);

    long getTotalActiveBooks();

    long getTotalAvailableBooks();
}