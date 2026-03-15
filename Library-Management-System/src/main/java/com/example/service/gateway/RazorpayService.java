package com.example.service.gateway;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.domain.PaymentType;
import com.example.model.Payment;
import com.example.model.SubscriptionPlan;
import com.example.model.User;
import com.example.payload.response.PaymentLinkResponse;
import com.example.service.SubscriptionPlanService;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final SubscriptionPlanService subscriptionPlanService;

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Value("${razorpay.callback.base-url:http://localhost:5173}")
    private String callbackBaseUrl;

    public PaymentLinkResponse createPaymentLink(User user, Payment payment) {

        try {

            RazorpayClient razorpayClient =
                    new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            Long amountInPaisa = payment.getAmount() * 100;

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", amountInPaisa);
            paymentLinkRequest.put("currency", "INR");
            paymentLinkRequest.put("description", payment.getDescription());

            // Customer Details
            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());

            if (user.getPhone() != null) {
                customer.put("contact", user.getPhone());
            }

            paymentLinkRequest.put("customer", customer);

            // Notification settings
            JSONObject notify = new JSONObject();
            notify.put("email", true);
            notify.put("sms", user.getPhone() != null);

            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);

            // Callback URL
            String successUrl =
                    callbackBaseUrl + "/payment-success/" + payment.getId();

            paymentLinkRequest.put("callback_url", successUrl);
            paymentLinkRequest.put("callback_method", "get");

            // Notes (metadata)
            JSONObject notes = new JSONObject();
            notes.put("user_id", user.getId());
            notes.put("payment_id", payment.getId());

            if (payment.getPaymentType() == PaymentType.MEMBERSHIP) {

                if (payment.getSubscription() != null) {
                    notes.put("subscription_id", payment.getSubscription().getId());
                    notes.put("plan", payment.getSubscription().getPlan().getPlanCode());
                }

                notes.put("type", PaymentType.MEMBERSHIP.toString());

            } else if (payment.getPaymentType() == PaymentType.FINE) {

                // If you implement fine system later
                // notes.put("fine_id", payment.getFine().getId());

                notes.put("type", PaymentType.FINE.toString());
            }

            paymentLinkRequest.put("notes", notes);

            PaymentLink paymentLink =
                    razorpayClient.paymentLink.create(paymentLinkRequest);

            PaymentLinkResponse response = new PaymentLinkResponse();
            response.setPayment_link_id(paymentLink.get("id"));
            response.setPayment_link_url(paymentLink.get("short_url"));

            return response;

        } catch (RazorpayException e) {
            log.error("Error creating Razorpay payment link", e);
            throw new RuntimeException("Error creating Razorpay payment link", e);
        }
    }

    public JSONObject fetchPaymentDetails(String paymentId) {

        try {

            RazorpayClient razorpay =
                    new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            com.razorpay.Payment payment =
                    razorpay.payments.fetch(paymentId);

            return payment.toJson();

        } catch (RazorpayException e) {

            log.error("Failed to fetch payment details for payment ID {}", paymentId, e);
            throw new RuntimeException(
                    "Failed to fetch payment details for payment ID: " + paymentId, e);
        }
    }

    public boolean isValidPayment(String paymentId) {

        try {

            JSONObject paymentDetails = fetchPaymentDetails(paymentId);

            String status = paymentDetails.optString("status");

            if (!"captured".equalsIgnoreCase(status)) {
                log.warn("Payment not captured. Current status: {}", status);
                return false;
            }

            long amount = paymentDetails.optLong("amount");
            long amountInRupees = amount / 100;

            JSONObject notes = paymentDetails.getJSONObject("notes");
            String paymentType = notes.optString("type");

            if (paymentType.equals(PaymentType.MEMBERSHIP.toString())) {

                String planCode = notes.optString("plan");

                SubscriptionPlan subscriptionPlan =
                        subscriptionPlanService.getBySubscriptionPlanCode(planCode);

                return amountInRupees == subscriptionPlan.getPrice();
            }

            else if (paymentType.equals(PaymentType.FINE.toString())) {

                // Example placeholder for fine validation
                // Long fineId = notes.getLong("fine_id");
                // Fine fine = fineService.getFineById(fineId);
                // return amountInRupees == fine.getAmount();

                return true;
            }

            return false;

        } catch (Exception e) {

            log.error("Payment validation failed", e);
            return false;
        }
    }
}