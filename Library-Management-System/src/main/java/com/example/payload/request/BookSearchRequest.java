package com.example.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchRequest {

    private String searchTerm;

    private Long genreId;

    private Boolean availableOnly = false;

    @Min(value = 0, message = "Page cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private Integer size = 10;

    private String sortBy = "createdAt";

    @Pattern(regexp = "ASC|DESC", message = "Sort direction must be ASC or DESC")
    private String sortDirection = "DESC";
}