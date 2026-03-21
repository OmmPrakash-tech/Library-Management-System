package com.example.event;

import com.example.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentSuccessEvent {

    private final Payment payment;

}