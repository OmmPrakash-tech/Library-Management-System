package com.example.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentStatus;
import com.example.domain.PaymentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;

@ManyToOne
private User user;

@ManyToOne
private Subscription subscription;

private PaymentType paymentType;

private PaymentStatus status;

@Enumerated(EnumType.STRING)
private PaymentGateway gateway;

private Long amount;

private String transactionId;

private String gatewayPaymentId;

private String gatewayOrderId;



private String gatewaySignature;

private String description;

private String failureReason;

@CreationTimestamp
private LocalDateTime initiatedAt;

private LocalDateTime completedAt;

@UpdateTimestamp
private LocalDateTime updatedAt;

@CreationTimestamp
private LocalDateTime createdAt;


}
