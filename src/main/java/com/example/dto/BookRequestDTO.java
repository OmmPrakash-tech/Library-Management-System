package com.example.dto;

import lombok.Data;

@Data
public class BookRequestDTO {
    private Long bookId;

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
}
