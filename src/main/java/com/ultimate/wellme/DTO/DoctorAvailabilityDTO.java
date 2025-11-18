package com.ultimate.wellme.DTO;

import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorAvailabilityDTO {
    
    private Long doctorId;
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
    private int maxPatientsPerSlot;
    private boolean active;
}
