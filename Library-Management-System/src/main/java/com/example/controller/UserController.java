package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.payload.dto.UserDTO;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

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

    userService.deleteUser(id);

    return ResponseEntity.ok("User deleted successfully");
}

@PutMapping("/update/{id}")
public ResponseEntity<UserDTO> updateUser(
        @PathVariable Long id,
        @RequestBody UserDTO userDTO) {

    UserDTO updatedUser = userService.updateUser(id, userDTO);

    return ResponseEntity.ok(updatedUser);
}


}