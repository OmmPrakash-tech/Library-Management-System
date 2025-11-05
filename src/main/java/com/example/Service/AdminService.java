package com.example.service;

import com.example.dto.BookDTO;
import com.example.dto.UserDTO;
import com.example.entity.Book;
import com.example.entity.BookRequest;
import com.example.entity.User;
import com.example.repository.BookRepository;
import com.example.repository.BookRequestRepository;
import com.example.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookRequestRepository bookRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===== USER MANAGEMENT =====

    public User addUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(false); // Needs approval
        return userRepository.save(user);
    }

    public User editUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setDepartment(userDTO.getDepartment());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ===== BOOK MANAGEMENT =====

    public Book addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setBookTitle(bookDTO.getBookTitle());
        book.setCategory(bookDTO.getCategory());
        book.setIsbn(bookDTO.getIsbn());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublishYear(bookDTO.getPublishYear());
        book.setPublisherName(bookDTO.getPublisherName());
        book.setNoOfCopies(bookDTO.getNoOfCopies());
        book.setPublishDate(bookDTO.getPublishDate());
        return bookRepository.save(book);
    }

    public Book editBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setBookTitle(bookDTO.getBookTitle());
        book.setCategory(bookDTO.getCategory());
        book.setIsbn(bookDTO.getIsbn());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublishYear(bookDTO.getPublishYear());
        book.setPublisherName(bookDTO.getPublisherName());
        book.setNoOfCopies(bookDTO.getNoOfCopies());
        book.setPublishDate(bookDTO.getPublishDate());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // ===== BOOK REQUEST MANAGEMENT =====

    public List<BookRequest> getAllBookRequests() {
        return bookRequestRepository.findAll();
    }

    public BookRequest approveRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("APPROVED");
        return bookRequestRepository.save(request);
    }

    public BookRequest rejectRequest(Long requestId) {
        BookRequest request = bookRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("REJECTED");
        return bookRequestRepository.save(request);
    }

    // ===== APPROVE/REJECT USER =====
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User rejectUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        return userRepository.save(user);
    }
}
