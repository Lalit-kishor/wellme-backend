package com.ultimate.wellme.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    
    @NotNull
    private Double order_amount;

    @NotBlank
    private String customer_phone;

    private String return_url;

    @NotNull
    private Long appointmentId;

}
