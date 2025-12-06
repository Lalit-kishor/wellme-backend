package com.ultimate.wellme.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDTO {
    
    private Long scheduleId;
    private String type;
    private String notes;
    private String paymentMethod;
}
