package com.example;

import com.example.entity.Admin;
import com.example.entity.Book;
import com.example.entity.User;
import com.example.repository.AdminRepository;
import com.example.repository.BookRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // ===== Add Admin =====
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setName("Super Admin");
            admin.setEmail("admin@library.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
        }

        // ===== Add User =====
        if (userRepository.count() == 0) {
            User user = new User();
            user.setUsername("John Doe");
            user.setEmail("john@library.com");
            user.setDepartment("CSE");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEnabled(true); // approved
            userRepository.save(user);
        }

        // ===== Add Books =====
        if (bookRepository.count() == 0) {
            Book book1 = new Book();
            book1.setBookTitle("Java Programming");
            book1.setCategory("New");
            book1.setIsbn("ISBN001");
            book1.setAuthor("Author A");
            book1.setPublishYear(2022);
            book1.setPublisherName("Tech Press");
            book1.setNoOfCopies(5);
            book1.setPublishDate(LocalDate.of(2022, 1, 10));
            bookRepository.save(book1);

            Book book2 = new Book();
            book2.setBookTitle("Spring Boot Guide");
            book2.setCategory("Old");
            book2.setIsbn("ISBN002");
            book2.setAuthor("Author B");
            book2.setPublishYear(2020);
            book2.setPublisherName("Code Press");
            book2.setNoOfCopies(3);
            book2.setPublishDate(LocalDate.of(2020, 5, 15));
            bookRepository.save(book2);
        }
    }
}
