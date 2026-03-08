package com.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exception.BookException;
import com.example.mapper.BookMapper;
import com.example.model.Book;
import com.example.payload.dto.BookDTO;
import com.example.payload.request.BookSearchRequest;
import com.example.payload.response.PageResponse;
import com.example.repository.BookRepository;
import com.example.service.BookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public BookDTO createBook(BookDTO bookDTO) {

        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new BookException(
                    "Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }

        Book book = bookMapper.toEntity(bookDTO);

        if (!book.isAvailableCopiesValid()) {
            throw new BookException(
                    "Available copies cannot exceed total copies");
        }

        Book savedBook = bookRepository.save(book);

        return bookMapper.toDTO(savedBook);
    }

    @Override
    @Transactional
    public List<BookDTO> createBooksBulk(List<BookDTO> bookDTOs) {

        return bookDTOs.stream()
                .map(this::createBook)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBookById(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found"));

        return bookMapper.toDTO(book);
    }

    @Override
    public BookDTO getBookByISBN(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookException("Book not found"));

        return bookMapper.toDTO(book);
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {

        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found"));

        bookMapper.updateEntityFromDTO(bookDTO, existingBook);

        if (!existingBook.isAvailableCopiesValid()) {
            throw new BookException(
                    "Available copies cannot exceed total copies");
        }

        Book savedBook = bookRepository.save(existingBook);

        return bookMapper.toDTO(savedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found"));

        book.setActive(false); // Soft delete

        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void hardDeleteBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found"));

        bookRepository.delete(book);
    }

    @Override
    public PageResponse<BookDTO> searchBooksWithFilters(
            BookSearchRequest searchRequest) {

        Pageable pageable = createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection());

        Page<Book> bookPage = bookRepository.searchBooksWithFilters(
                searchRequest.getSearchTerm(),
                searchRequest.getGenreId(),
                searchRequest.getAvailableOnly(),
                pageable);

        return convertToPageResponse(bookPage);
    }

    @Override
    public long getTotalActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    @Override
    public long getTotalAvailableBooks() {
        return bookRepository.countAvailableBooks();
    }

    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        size = Math.min(size, 10);
        size = Math.max(size, 1);

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "title";
        }

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    private PageResponse<BookDTO> convertToPageResponse(
            Page<Book> books) {

        List<BookDTO> bookDTOs = books.getContent()
                .stream()
                .map(bookMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                bookDTOs,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isLast(),
                books.isFirst(),
                books.isEmpty());
    }
}