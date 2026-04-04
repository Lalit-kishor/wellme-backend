package com.ultimate.wellme.controllers;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cashfree.Cashfree;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ultimate.wellme.services.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    
    @Value("${cashfree.app.id}")
    private String cashfreeAppId;
    
    @Value("${cashfree.secret.key}")
    private String cashfreeSecretKey;
    
    @Value("${cashfree.environment}")
    private String cashfreeEnvironment;

    @Autowired
    private PaymentService paymentService;
    
    @PostMapping(value="/cashfree")
    public ResponseEntity<String> handleCashFreeWebHook(HttpServletRequest request) throws IOException {

        System.out.println("In webhook controller");

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            
            bufferedReader =request.getReader();
            String line;

            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String rawBody = stringBuilder.toString();
            String signature = request.getHeader("x-webhook-signature");
            String timestamp = request.getHeader("x-webhook-timestamp");

            // System.out.println("webhook raw body: " + rawBody);

            // Configure Cashfree client with credentials
            Cashfree.XClientId = cashfreeAppId;
            Cashfree.XClientSecret = cashfreeSecretKey;
            Cashfree.XEnvironment = "SANDBOX".equals(cashfreeEnvironment) ? Cashfree.SANDBOX : Cashfree.PRODUCTION;

            Cashfree cashfree = new Cashfree();
            cashfree.PGVerifyWebhookSignature(signature, rawBody, timestamp);

            // System.out.println("Signature has been verified successfully");

            // Process the raw body
            Gson gson = new Gson();
            JsonObject webhookData = gson.fromJson(rawBody, JsonObject.class);
            String eventType = webhookData.get("type").getAsString();
            JsonObject data = webhookData.getAsJsonObject("data");

            if("ORDER_SUCCESS".equals(eventType) || "PAYMENT_SUCCESS_WEBHOOK".equals(eventType)) {
                
                JsonObject order = data.getAsJsonObject("order");
                JsonObject payment = data.getAsJsonObject("payment");
                // System.out.println("#########################");
                // System.out.println(payment);


                String orderId = order.get("order_id").getAsString();
                String cfPaymentId = payment.get("cf_payment_id").getAsString();

                // System.out.println("cfPaymentId in  webhook controller is: " + cfPaymentId);

                // System.out.println("Payment success for order: " + orderId);
                paymentService.markOrderPaid(orderId, cfPaymentId);

            } else if ("ORDER_FAILED".equals(eventType) || "PAYMENT_FAILED_WEBHOOK".equals(eventType)) {
                
                JsonObject order = data.getAsJsonObject("order");
                String orderId = order.get("order_id").getAsString();

                paymentService.markOrderFailed(orderId);
            }

            // System.out.println("Event type: " + eventType);
            // System.out.println("Webhook Data: " + data);

            return ResponseEntity.status(200).body("Webhook received and processed");

        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.status(400).body("Signature verification failed");
        } finally {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
}


/*
 * 
 * import com.cashfree.*;
 * import com.google.gson.Gson;
 * import com.google.gson.JsonObject;
 * 
 * @RestController
 * public class WebhookController {
 * 
 * @PostMapping("/webhook")
 * public ResponseEntity<String> handleWebhook(HttpServletRequest request)
 * throws IOException {
 * Cashfree.XClientId = "<x-client-id>";
 * Cashfree.XClientSecret = "<x-client-secret>";
 * Cashfree.XEnvironment = Cashfree.SANDBOX;
 * 
 * StringBuilder stringBuilder = new StringBuilder();
 * BufferedReader bufferedReader = request.getReader();
 * String line;
 * while ((line = bufferedReader.readLine()) != null) {
 * stringBuilder.append(line).append('\n');
 * }
 * 
 * String rawBody = stringBuilder.toString();
 * String signature = request.getHeader("x-webhook-signature");
 * String timestamp = request.getHeader("x-webhook-timestamp");
 * 
 * try {
 * Cashfree cashfree = new Cashfree();
 * // Verify signature - throws exception if invalid
 * cashfree.PGVerifyWebhookSignature(signature, rawBody, timestamp);
 * 
 * // Parse the raw body to access webhook data
 * Gson gson = new Gson();
 * JsonObject webhookData = gson.fromJson(rawBody, JsonObject.class);
 * 
 * String eventType = webhookData.get("type").getAsString();
 * JsonObject data = webhookData.getAsJsonObject("data");
 * 
 * System.out.println("Event Type: " + eventType);
 * // Process based on event type (PAYMENT_SUCCESS, etc.)
 * 
 * return ResponseEntity.ok("Webhook processed");
 * } catch (Exception e) {
 * return ResponseEntity.status(400).body("Verification failed");
 * }
 * }
 * }
 * 
 * 
 * 
 */