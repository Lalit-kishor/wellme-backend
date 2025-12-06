package com.ultimate.wellme.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ultimate.wellme.models.Appointment;
import com.ultimate.wellme.models.AppointmentSchedule;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.DTO.AppointmentDTO;
import com.ultimate.wellme.Repos.AppointmentScheduleRepo;
import com.ultimate.wellme.config.ApiResponse;
import com.ultimate.wellme.services.AppointmentService;
import com.ultimate.wellme.services.JwtService;
import com.ultimate.wellme.services.PatientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PatientController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentScheduleRepo scheduleRepo;

    @Autowired
    private AppointmentService appointmentService;
    
    @PostMapping("/bookAppointment")
    public ResponseEntity<ApiResponse> bookAppointment(@RequestBody AppointmentDTO appointmentDTO, HttpServletRequest request) {

        try {
            String token = null;
            if(request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
            // Extract Patient Info
            Long patientId = jwtService.extractUserId(token);
            User patient_user = patientService.getPatientById(patientId);
            
            System.out.println(appointmentDTO.getScheduleId());

            // Extract Doctor Info
            Optional<AppointmentSchedule> schedule = scheduleRepo.findById(appointmentDTO.getScheduleId());

            if(!schedule.isPresent()) return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Schedule not found"), HttpStatus.NOT_FOUND);

            System.out.println("Schedule is present with given id...");
            User doctor_user = schedule.get().getDoctor();

            // In case of lazy loading of associated entities, Hibernate creates a placeholder object - a proxy (User$HibernateProxy) - that is  a subclass of the declared type(User) 
            //  User real_user = (User) Hibernate.unproxy(doctor_user);
            // Then proceed with below steps:


            Doctor doctor = (Doctor)doctor_user;
    
            Appointment newAppointment = new Appointment();
            newAppointment.setConsultationFee(doctor.getConsultationFee());
            newAppointment.setPatient(patient_user);
            newAppointment.setDoctor(doctor_user);
            newAppointment.setDate(schedule.get().getDate());
            newAppointment.setStartTime(schedule.get().getStartTime());
            newAppointment.setNotes(appointmentDTO.getNotes());
            newAppointment.setType(appointmentDTO.getType());
            newAppointment.setAppointmentSchedule(schedule.get());
            newAppointment.setStatus(Appointment.AppointmentStatus.PENDING);
    
            appointmentService.save(newAppointment);
    
            return new ResponseEntity<ApiResponse> (new ApiResponse(true, "Appointment Booked Successfully."), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Appointment Booking Failed..." + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
