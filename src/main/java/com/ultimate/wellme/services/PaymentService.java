package com.ultimate.wellme.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cashfree.ApiException;
import com.cashfree.ApiResponse;
import com.cashfree.Cashfree;
import com.cashfree.model.CreateOrderRequest;
import com.cashfree.model.CustomerDetails;
import com.cashfree.model.OrderEntity;
import com.cashfree.model.OrderMeta;
import com.ultimate.wellme.Repos.OrderRepository;
import com.ultimate.wellme.models.Appointment;
import com.ultimate.wellme.models.Order;
import com.ultimate.wellme.models.Appointment.AppointmentStatus;

@Service
public class PaymentService {

    @Value("${cashfree.api.version}")
    private String apiVersion;

    @Value("${cashfree.webhook.url}")
    private String webhookUrl;

    @Autowired
    private OrderRepository orderRepository;
    
    public String createOrder(Double amount, String name, String phone, Long patientId) {

        try {
            
            // 1. Create Customer Details Object
            CustomerDetails customerDetails = new CustomerDetails();
            customerDetails.setCustomerId(String.valueOf(patientId));
            customerDetails.setCustomerName(name);
            customerDetails.setCustomerPhone(phone);

            OrderMeta orderMeta =  new OrderMeta();
            // orderMeta.setReturnUrl("http://localhost:3000/payment/status?order_id={order_id}");

            // Add the Notify (webhook) url
            orderMeta.setNotifyUrl(webhookUrl);

            // 2. Create order request object
            CreateOrderRequest request = new CreateOrderRequest();
            request.setOrderAmount(amount);
            request.setOrderCurrency("INR");
            request.setCustomerDetails(customerDetails);
            request.setOrderMeta(orderMeta);

            request.setOrderId("ORDER_" + UUID.randomUUID().toString());

            // Call Cashfree API
            Cashfree cashfree = new Cashfree();
            String paymentSessionId = null;

            try {
                ApiResponse<OrderEntity> response = cashfree.PGCreateOrder(apiVersion, request, null, null, null);
                System.out.println("Order Id: "  + response.getData().getOrderId()); 
                paymentSessionId = response.getData().getPaymentSessionId();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

            return paymentSessionId;
        } catch (Exception e) {
            System.out.println("Exception occured while creating the order in service class: ");
            e.printStackTrace();
        }

        return null;
    }

    @Transactional
    public void markOrderPaid(String orderId, String cfPaymentId) {
        
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

        if(orderOpt.isPresent()) {
            Order order = orderOpt.get(); 

            if(!"PAID".equals(order.getStatus())) {
                order.setStatus("PAID");
                order.setPaymentId(cfPaymentId);
                // orderRepository.save(order);
                System.out.println("2nd if of markOrderPaid function");
                Appointment appointment = order.getAppointment();

                if(AppointmentStatus.PENDING.equals(appointment.getStatus())) {
                    System.out.println("3rd if of markOrderPaid function");
                    appointment.setStatus(AppointmentStatus.CONFIRMED);
                    appointment.setMeetingRoomId("doc-" + appointment.getDoctor().getId()
                            + "-apt-" + appointment.getAppointmentId()
                            + "-" + UUID.randomUUID().toString().substring(0, 8));
                }
                // appointmentRepo.save(appointment);
                
            }
        }
    }

    @Transactional
    public void markOrderFailed(String orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();

            if (!"FAILED".equals(order.getStatus())) {
                order.setStatus("FAILED");
                // orderRepository.save(order);
            }
        }
    }
}
