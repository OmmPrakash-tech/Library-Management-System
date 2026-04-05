package com.example.service;

import java.util.List;

import com.example.model.User;
import com.example.payload.dto.UpdateUserDTO;
import com.example.payload.dto.UserDTO;

public interface UserService {

    public User getCurrentUser();

    public List<UserDTO> getAllUsers();

    User findById(Long id) throws RuntimeException;


    void deleteUser(Long id);

    UserDTO updateUser(Long id, UpdateUserDTO dto);

}