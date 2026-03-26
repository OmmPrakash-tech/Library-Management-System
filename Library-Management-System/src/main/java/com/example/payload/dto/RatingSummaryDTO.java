package com.example.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingSummaryDTO {

    private Double averageRating;   // ⭐ Average rating (e.g., 4.3)

    private Long totalReviews;      // 🧾 Total number of reviews
}