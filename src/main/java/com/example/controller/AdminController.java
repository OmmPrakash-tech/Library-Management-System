package com.example.controller;

import com.example.dto.BookDTO;
import com.example.dto.UserDTO;
import com.example.entity.Book;
import com.example.entity.BookRequest;
import com.example.entity.User;
import com.example.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ===== USER MANAGEMENT =====
    @PostMapping("/users")
    public User addUser(@RequestBody UserDTO userDTO) {
        return adminService.addUser(userDTO);
    }

    @PutMapping("/users/{id}")
    public User editUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return adminService.editUser(id, userDTO);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{id}/approve")
    public User approveUser(@PathVariable Long id) {
        return adminService.approveUser(id);
    }

    @PutMapping("/users/{id}/reject")
    public User rejectUser(@PathVariable Long id) {
        return adminService.rejectUser(id);
    }

    // ===== BOOK MANAGEMENT =====
    @PostMapping("/books")
    public Book addBook(@RequestBody BookDTO bookDTO) {
        return adminService.addBook(bookDTO);
    }

    @PutMapping("/books/{id}")
    public Book editBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return adminService.editBook(id, bookDTO);
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        adminService.deleteBook(id);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return adminService.getAllBooks();
    }

    // ===== BOOK REQUEST MANAGEMENT =====
    @GetMapping("/requests")
    public List<BookRequest> getAllBookRequests() {
        return adminService.getAllBookRequests();
    }

    @PutMapping("/requests/{id}/approve")
    public BookRequest approveRequest(@PathVariable Long id) {
        return adminService.approveRequest(id);
    }

    @PutMapping("/requests/{id}/reject")
    public BookRequest rejectRequest(@PathVariable Long id) {
        return adminService.rejectRequest(id);
    }
}
