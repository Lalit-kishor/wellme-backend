package com.ultimate.wellme.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.AppointmentScheduleRepo;
import com.ultimate.wellme.Repos.DoctorAvailabilityRepo;
import com.ultimate.wellme.models.AppointmentSchedule;
import com.ultimate.wellme.models.AppointmentStatus;
import com.ultimate.wellme.models.DayOfWeek;
import com.ultimate.wellme.models.DoctorAvailability;

@Service
public class SlotGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SlotGenerationService.class);

    @Autowired
    private DoctorAvailabilityRepo doctorAvailabilityRepo;

    @Autowired
    private AppointmentScheduleRepo appointmentScheduleRepo;

    public void generateSlotsForNextWeek() {
        
        List<DoctorAvailability> availabilities = doctorAvailabilityRepo.findByActiveTrue();

        int totalSlotsGenerated = 0;
        for(DoctorAvailability availability: availabilities) {
            totalSlotsGenerated += generateSlotsForAvailability(availability);
        }

        logger.info("Generated {} slots for {} doctors", totalSlotsGenerated, availabilities.size());
    }

    private int generateSlotsForAvailability(DoctorAvailability availability) {
        
        int slotsGenerated = 0;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);

        for(LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {

            if(matchesAvailabilityDay(availability.getDay(), date)) {
                
                // Check if slot already exists for this date
                boolean slotExists = appointmentScheduleRepo.existsByDoctorAndDate(availability.getDoctor(), date);

                if(!slotExists) slotsGenerated += createSlotsForDate(availability, date);
            }
        }
        logger.info("{} slots generated", slotsGenerated);
        return slotsGenerated;
    }

    private int createSlotsForDate(DoctorAvailability availability, LocalDate date) {
        
       List<AppointmentSchedule> slots = new ArrayList<>();

       LocalTime currentTime = availability.getStartTime();
       LocalTime endTime = availability.getEndTime();
       int slotDuration = availability.getSlotDurationMinutes();

       while(currentTime.plusMinutes(slotDuration).isBefore(endTime) || currentTime.plusMinutes(slotDuration).equals(endTime)) {
            AppointmentSchedule slot = new AppointmentSchedule();
            slot.setDoctor(availability.getDoctor());
            slot.setDate(date);
            slot.setStartTime(currentTime);
            slot.setEndTime(currentTime.plusMinutes(slotDuration));
            slot.setStatus(AppointmentStatus.AVAILABLE);
            slot.setCreatedBy("SCHEDULER_JOB");

            slots.add(slot);
            currentTime =  currentTime.plusMinutes(slotDuration);
       }

       // Batch save all slots
       appointmentScheduleRepo.saveAll(slots);

       logger.debug("Generated {} slots for doctor {} on {}",
               slots.size(), availability.getDoctor().getId(), date);

        return slots.size();
    }

    private boolean matchesAvailabilityDay(DayOfWeek availabilityDay, LocalDate date) {
       java.time.DayOfWeek javaDayOfWeek = date.getDayOfWeek();
       return availabilityDay.name().equals(javaDayOfWeek.name());
    }


}
