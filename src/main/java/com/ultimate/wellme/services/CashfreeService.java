package com.ultimate.wellme.services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ultimate.wellme.DTO.CreateOrderResponse;
import com.ultimate.wellme.Repos.AppointmentRepo;
import com.ultimate.wellme.Repos.OrderRepository;
import com.ultimate.wellme.models.Appointment;
import com.ultimate.wellme.models.Order;

@Service
public class CashfreeService {
    @Value("${cashfree.base.url}")
    private String baseUrl;

    @Value("${cashfree.app.id}")
    private String appId;

    @Value("${cashfree.secret.key}")
    private String secretKey;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppointmentRepo appointmentRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    public String createOrder(Double amount, String phone, String returnUrl, Long appointmentId) {

        String orderId = "ORD_" + UUID.randomUUID();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", appId);
        headers.set("x-client-secret", secretKey);
        headers.set("x-api-version", "2023-08-01");

        Map<String, Object> body = new HashMap<>();
        body.put("order_id", orderId);
        body.put("order_amount", amount);
        body.put("order_currency", "INR");

        Map<String, Object> customer = new HashMap<>();
        customer.put("customer_id", orderId);
        customer.put("customer_phone", phone);

        body.put("customer_details", customer);

        Map<String, Object> orderMeta = new HashMap<>();
        orderMeta.put("return_url", returnUrl !=null ? returnUrl + "?order_id={order_id}" : null);

        body.put("order_meta", orderMeta);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<CreateOrderResponse> response = restTemplate.postForEntity(
                                                                                                baseUrl + "/orders",
                                                                                                entity,
                                                                                                CreateOrderResponse.class
        );

        if(response.getBody() == null) {
            throw new RuntimeException("Cashfree API returned null response body");
        }
        
        String sessionId =  response.getBody().getPayment_session_id();

        if(sessionId == null) {
            throw new RuntimeException("Payment session ID not found in cashfree response");
        }

        // Fetch the appointment
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        Order order = new Order();
        order.setOrderId(orderId);
        order.setAmount(amount);
        order.setCustomerPhone(phone);
        order.setStatus("PENDING");
        order.setAppointment(appointment);

        // Maintain bidirectional relationship - add order to appointment's orders list
        if (appointment.getOrders() == null) {
            appointment.setOrders(new java.util.ArrayList<>());
        }
        appointment.getOrders().add(order);

        orderRepository.save(order);

        return  sessionId;
    }

    public Map<String, Object> getOrderStatus(String orderId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", appId);
        headers.set("x-client-secret", secretKey);
        headers.set("x-api-version", "2023-08-01");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                                                                                        baseUrl + "/orders/" + orderId, 
                                                                                        HttpMethod.GET, 
                                                                                        entity, 
                                                                                        new ParameterizedTypeReference<Map<String, Object>>(){});

        return response.getBody();
    }
}
