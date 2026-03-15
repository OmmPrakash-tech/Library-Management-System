package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.domain.PaymentGateway;
import com.example.domain.PaymentStatus;
import com.example.event.publisher.PaymentEventPublisher;
import com.example.mapper.PaymentMapper;
import com.example.model.Payment;
import com.example.model.Subscription;
import com.example.model.User;
import com.example.payload.dto.PaymentDTO;
import com.example.payload.request.PaymentInitiateRequest;
import com.example.payload.request.PaymentVerifyRequest;
import com.example.payload.response.PaymentInitiateResponse;
import com.example.payload.response.PaymentLinkResponse;
import com.example.repository.PaymentRepository;
import com.example.repository.SubscriptionRepository;
import com.example.repository.UserRepository;
import com.example.service.PaymentService;
import com.example.service.gateway.RazorpayService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class PaymentServiceImpl implements PaymentService{
    
private final UserRepository userRepository;
private final SubscriptionRepository subscriptionRepository;
private final PaymentRepository paymentRepository;
private final RazorpayService razorpayService;
private final PaymentMapper paymentMapper;
private final PaymentEventPublisher paymentEventPublisher;

@Override
public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {

    User user = userRepository.findById(request.getUserId()).get();

    Payment payment = new Payment();
    payment.setUser(user);
    payment.setPaymentType(request.getPaymentType());
    payment.setGateway(request.getGateway());
    payment.setAmount(request.getAmount());

    payment.setDescription(request.getDescription());
    payment.setStatus(PaymentStatus.PENDING);
    payment.setTransactionId("TXN-" + UUID.randomUUID());
    payment.setInitiatedAt(LocalDateTime.now());

   if (request.getSubscriptionId() != null) {

    Subscription sub = subscriptionRepository
            .findById(request.getSubscriptionId())
            .orElseThrow(() -> new RuntimeException("Subscription not found"));

    payment.setSubscription(sub);
}

payment = paymentRepository.save(payment);

    PaymentInitiateResponse response = new PaymentInitiateResponse();

if (request.getGateway() == PaymentGateway.RAZORPAY) {

    PaymentLinkResponse paymentLinkResponse = razorpayService.createPaymentLink(
            user, payment
    );

    response = PaymentInitiateResponse.builder()
            .paymentId(payment.getId())
            .gateway(payment.getGateway())
            .checkoutUrl(paymentLinkResponse.getPayment_link_url())
            .transactionId(paymentLinkResponse.getPayment_link_id())
            .amount(payment.getAmount())
            .description(payment.getDescription())
            .success(true)
            .message("Payment initiated successfully")
            .build();

           payment.setGatewayOrderId(paymentLinkResponse.getPayment_link_id());

}

payment.setStatus(PaymentStatus.PROCESSING);
paymentRepository.save(payment);

return response;
}

@Override
public PaymentDTO verifyPayment(PaymentVerifyRequest req) throws RuntimeException {

    JSONObject paymentDetails = razorpayService.fetchPaymentDetails(
            req.getRazorpayPaymentId()
    );

    JSONObject notes = paymentDetails.getJSONObject("notes");

    // Access specific fields inside 'notes'
    Long paymentId = Long.parseLong(notes.optString("payment_id"));

    Payment payment = paymentRepository.findById(paymentId).get();

    boolean isValid = razorpayService.isValidPayment(req.getRazorpayPaymentId());

    if (PaymentGateway.RAZORPAY == payment.getGateway()) {

        if (isValid) {
            payment.setGatewayOrderId(req.getRazorpayPaymentId());
        }

        if (isValid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setCompletedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);

            // publish payment success event

            paymentEventPublisher.publishPaymentSuccessEvent(payment);
        }
    }

    return paymentMapper.toDTO(payment);
}

@Override
public Page<PaymentDTO> getAllPayments(Pageable pageable) {
    Page<Payment> payments = paymentRepository.findAll(pageable);
    return payments.map(paymentMapper::toDTO);
}

}
