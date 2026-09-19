package com.ultimate.wellme.mapper;

import com.ultimate.wellme.DTO.SlotResponse;
import com.ultimate.wellme.models.AppointmentSchedule;

public class SlotMapper {
     private SlotMapper() {}
 
    public static SlotResponse toResponse(AppointmentSchedule schedule) {
        return new SlotResponse(
            schedule.getId(),
            schedule.getDate(),
            schedule.getStartTime(),
            schedule.getEndTime(),
            schedule.getStatus().name() // drop .name() if status is already a String on your entity
        );
    }
}
