package com.ultimate.wellme.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ultimate.wellme.DTO.CreateOrderRequest;
import com.ultimate.wellme.DTO.CreateOrderResponse;
import com.ultimate.wellme.config.ApiResponse;
import com.ultimate.wellme.models.Appointment;
import com.ultimate.wellme.Repos.OrderRepository;
import com.ultimate.wellme.services.AppointmentService;
import com.ultimate.wellme.services.CashfreeService;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private CashfreeService cashfreeService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private OrderRepository orderRepository;
    
    // creating an order is a security handshake between the business logic and financial service (Payment Gateway)
    @PostMapping("/create-order")
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        System.out.println("Reached inside create-order controller");
        String sessionId = cashfreeService.createOrder(
            createOrderRequest.getOrder_amount(), 
            createOrderRequest.getCustomer_phone(), 
            createOrderRequest.getReturn_url(),
            createOrderRequest.getAppointmentId()
        );
        System.out.println("payment_session_id is: " + sessionId);
        
        return new CreateOrderResponse(sessionId);
    }

    @GetMapping("/order-status/{order_id}")
    public Map<String, Object> getOrderStatus(@PathVariable String order_id) {
        return cashfreeService.getOrderStatus(order_id);
    }

    @PostMapping("/confirm-payment/{order_id}")
    public ApiResponse confirmPayment(@PathVariable String order_id) {
        try {
            // Get order status from Cashfree
            Map<String, Object> orderStatus = cashfreeService.getOrderStatus(order_id);
            String paymentStatus = (String) orderStatus.get("order_status");

            // Find order in database
            com.ultimate.wellme.models.Order order = orderRepository.findByOrderId(order_id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if ("PAID".equalsIgnoreCase(paymentStatus)) {
                // Update order status
                order.setStatus("PAID");
                order.setPaymentId((String) orderStatus.get("cf_order_id"));
                orderRepository.save(order);

                // Update appointment status to CONFIRMED
                appointmentService.updateAppointmentStatus(
                    order.getAppointment().getAppointmentId(), 
                    Appointment.AppointmentStatus.CONFIRMED
                );

                return new ApiResponse(true, "Payment confirmed and appointment booked successfully");
            } else {
                order.setStatus("FAILED");
                orderRepository.save(order);
                return new ApiResponse(false, "Payment failed or pending");
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error confirming payment: " + e.getMessage());
        }
    }
}
