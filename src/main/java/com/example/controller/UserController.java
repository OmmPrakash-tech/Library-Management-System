package com.example.controller;

import com.example.dto.BookRequestDTO;
import com.example.entity.Book;
import com.example.entity.BookRequest;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ===== VIEW ALL BOOKS =====
    @GetMapping("/books")
    public List<Book> viewBooks() {
        return userService.getAllBooks();
    }

    // ===== REQUEST A BOOK =====
    @PostMapping("/requests")
    public BookRequest requestBook(Authentication authentication, @RequestBody BookRequestDTO requestDTO) {
        String userEmail = authentication.getName();
        return userService.requestBook(userEmail, requestDTO);
    }

    // ===== VIEW USER REQUEST STATUS =====
    @GetMapping("/requests")
    public List<BookRequest> viewUserRequests(Authentication authentication) {
        String userEmail = authentication.getName();
        return userService.getUserRequests(userEmail);
    }
}
