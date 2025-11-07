package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;

	public String getToken() {
		return token;
	}



	public AuthResponse(String token) {
		this.token=token;
		// TODO Auto-generated constructor stub
	}

	public void setToken(String token) {
		this.token = token;
	}
}
