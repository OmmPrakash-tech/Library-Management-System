package com.example.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.payload.dto.UpdateUserDTO;
import com.example.payload.dto.UserDTO;
import com.example.repository.UserRepository;
import com.example.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="https://library-management-system-3d9t.onrender.com")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    @GetMapping("/profile")
public ResponseEntity<User> getUserProfile() throws Exception {
    return ResponseEntity.ok(
            userService.getCurrentUser()
    );
}

@DeleteMapping("/delete/{id}")
public ResponseEntity<String> deleteUser(@PathVariable Long id) {

    if (!userRepository.existsById(id)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found");
    }

    userRepository.deleteById(id);

    return ResponseEntity.ok("User deleted successfully");
}

@PutMapping("/update/{id}")
public ResponseEntity<UserDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UpdateUserDTO dto) {

    UserDTO updatedUser = userService.updateUser(id, dto);

    return ResponseEntity.ok(updatedUser);
}

@GetMapping("/{id}")
public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return ResponseEntity.ok(userMapper.toDTO(user));
}

}