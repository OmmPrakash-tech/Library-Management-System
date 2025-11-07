package com.example.service;

import com.example.dto.BookRequestDTO;
import com.example.entity.Book;
import com.example.entity.BookRequest;
import com.example.entity.User;
import com.example.repository.BookRepository;
import com.example.repository.BookRequestRepository;
import com.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    // ===== VIEW ALL BOOKS =====
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ===== REQUEST A BOOK =====
    public BookRequest requestBook(String userEmail, BookRequestDTO requestDTO) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        BookRequest bookRequest = new BookRequest();
        bookRequest.setUser(user);
        bookRequest.setBook(book);
        bookRequest.setStatus("PENDING");
        return bookRequestRepository.save(bookRequest);
    }

    // ===== VIEW REQUEST STATUS =====
    public List<BookRequest> getUserRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookRequestRepository.findByUser(user);
    }
}
