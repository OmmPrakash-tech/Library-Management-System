package com.example.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    private String email;         // optional
    private String password;      // optional
    private String fullName;
    private String phone;
    private String profileImage;
}