package com.example.payload.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "Book ID is mandatory")
    private Long bookId;

    @NotNull(message = "Rating is mandatory")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @NotBlank(message = "Review text is mandatory")
    @Size(min = 10, max = 2000)
    private String reviewText;

    @Size(max = 200)
    private String title;
}
