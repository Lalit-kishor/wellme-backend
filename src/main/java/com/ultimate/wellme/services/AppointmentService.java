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

    public Appointment updateAppointmentStatus(Long appointmentId, Appointment.AppointmentStatus status) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        appointment.setStatus(status);
        System.out.println("In updateAppointmentStatus function");
        return appointmentRepo.save(appointment);
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
    }
}
