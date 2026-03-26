package com.example.payload.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookReviewDTO {

    private Long id;

    // ❗ Optional: Make READ_ONLY (user should not send this manually)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;

    private String userName;

    // ❌ Removed email (avoid exposing sensitive info)
    // private String userEmail;

    @NotNull(message = "Book ID is mandatory")
    private Long bookId;

    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;

    @NotNull(message = "Rating is mandatory")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Review text is mandatory")
    @Size(min = 10, max = 1000, message = "Review must be between 10 and 1000 characters")
    private String reviewText;

    @Size(max = 200, message = "Review title must not exceed 200 characters")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;

    // 🔥 Optional (very useful for frontend)
    private Boolean isOwner;
}