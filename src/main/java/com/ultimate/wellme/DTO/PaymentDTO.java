package com.ultimate.wellme.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentDTO {
    private Long appointmentId;
    private String paymentMethod;
    private double amount;  
}
