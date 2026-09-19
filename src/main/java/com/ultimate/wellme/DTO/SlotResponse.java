package com.ultimate.wellme.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotResponse(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String status
) {} 
   
