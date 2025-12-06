package com.ultimate.wellme.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.AppointmentRepo;
import com.ultimate.wellme.models.Appointment;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepo appointmentRepo;

    public Appointment save(Appointment appointment) {
        return appointmentRepo.save(appointment);
    }
}
