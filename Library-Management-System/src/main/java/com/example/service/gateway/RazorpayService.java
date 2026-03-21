package com.example.service.gateway;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.model.Payment;
import com.example.payload.response.RazorpayOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    // ✅ Initialize once
    @jakarta.annotation.PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
    }

    // 🔥 CREATE ORDER (CORE METHOD)
    public RazorpayOrderResponse createOrder(Payment payment) {

        try {
            log.info("Creating Razorpay order for paymentId={}", payment.getId());

            JSONObject options = new JSONObject();

            // ⚠️ Razorpay uses paisa
            options.put("amount", payment.getAmount() * 100);
            options.put("currency", "INR");

            // receipt helps tracking
            options.put("receipt", payment.getTransactionId());

            // optional metadata
            JSONObject notes = new JSONObject();
            notes.put("payment_id", payment.getId());
            options.put("notes", notes);

Order order = razorpayClient.orders.create(options);

String orderId = order.get("id");

Object amountObj = order.get("amount");
Long amount = ((Number) amountObj).longValue(); // ✅ BEST WAY

return new RazorpayOrderResponse(
        orderId,
        amount,
        order.get("currency")
);

        } 
        catch (Exception e) {
    log.error("Razorpay error message: {}", e.getMessage());
    e.printStackTrace();   // 👈 MUST ADD
    throw new RuntimeException("Error creating Razorpay order", e);
}
    }

    // 🔐 SIGNATURE VERIFICATION
    public boolean verifySignature(String orderId, String paymentId, String signature) {

        try {
            String payload = orderId + "|" + paymentId;

            String generatedSignature = hmacSHA256(payload, razorpayKeySecret);

            return generatedSignature.equals(signature);

        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }

    // 🔧 HMAC SHA256
    private String hmacSHA256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        return bytesToHex(hash);
    }

    // 🔧 HEX CONVERSION
    private String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder(2 * hash.length);

        for (byte b : hash) {
            String hexChar = Integer.toHexString(0xff & b);
            if (hexChar.length() == 1) hex.append('0');
            hex.append(hexChar);
        }

        return hex.toString();
    }
}