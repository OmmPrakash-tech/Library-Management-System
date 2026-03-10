package com.example.payload.dto;

import java.time.LocalDateTime;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import jakarta.persistence.Column;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPlanDTO {

    private Long id;

    @NotNull(message="Plan Code is mandatory")
private String planCode;

@NotNull(message="Plan name is mandatory")
private String name;

private String description;

@NotNull(message="Duration is mandatory")
@Positive(message="Duration must be positive")
private Integer durationDays;

@NotNull(message="Price is mandatory")
@Positive(message="Price is mandatory")
private Long price;

private String currency = "INR";

@NotNull(message="Max books allowed is mandatory")
@Positive(message = "Max books must be positive")
private Integer maxBooksAllowed;

@NotNull(message="Max days per book is mandatory")
@Positive(message = "max days must be positive")
private Integer maxDaysPerBook;

private Integer displayOrder;

private Boolean isActive;

private Boolean isFeatured;

private String badgeText;

private String adminNotes;




private LocalDateTime createdAt;


private LocalDateTime updatedAt;

private String createdBy;

private String updatedBy;



}
