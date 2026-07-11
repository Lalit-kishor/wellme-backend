package com.ultimate.wellme.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ultimate.wellme.DTO.DoctorAvailabilityDTO;
import com.ultimate.wellme.DTO.DoctorAvailabilityDetails;
import com.ultimate.wellme.Repos.AppointmentScheduleRepo;
import com.ultimate.wellme.Repos.UserRepo;
import com.ultimate.wellme.config.ApiResponse;
import com.ultimate.wellme.models.AppointmentSchedule;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.DoctorAvailability;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.DoctorAvailabilityService;
import com.ultimate.wellme.services.DoctorService;
import com.ultimate.wellme.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/doctor")
@EnableMethodSecurity(prePostEnabled = true)
public class DoctorController {

    @Autowired
    JwtService jwtService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;

    @Autowired
    private AppointmentScheduleRepo appointmentScheduleRepo;

    @Autowired
    private UserRepo userRepo;
    
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/addAvailability")
    public ResponseEntity<ApiResponse> addAvailability(@RequestBody DoctorAvailabilityDetails doctorAvailabilityDetails, HttpServletRequest request) {

        System.out.println("Data received from frontend: " + doctorAvailabilityDetails);

        try {
            // Get the JWT token from request

            String token = null;
            if(request.getCookies() != null) {
                for(Cookie cookie : request.getCookies()) {
                    if("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            Long doctorId = jwtService.extractUserId(token);

            User doctor = userRepo.getReferenceById(doctorId);


            // Optional<User> doctor = doctorService.getDoctorById(doctorId);

            // if(!doctor.isPresent()) return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Doctor Not found"),
            //             HttpStatus.NOT_FOUND);

            // 👉🏽 String role = authentication.getAuthorities().iterator().next().getAuthority();



            List<DoctorAvailabilityDTO> availabilityDTO = doctorAvailabilityDetails.getAvailability();
            List<DoctorAvailability> availabilities = new ArrayList<>();

            for(DoctorAvailabilityDTO  dto: availabilityDTO) {
                DoctorAvailability doctorAvailability = new DoctorAvailability();
                doctorAvailability.setDay(dto.getDay());
                doctorAvailability.setStartTime(dto.getStartTime());
                doctorAvailability.setEndTime(dto.getEndTime());
                doctorAvailability.setActive(dto.isActive());
                doctorAvailability.setDoctor(doctor);

                availabilities.add(doctorAvailability);
            }
           

            doctorAvailabilityService.saveAll(availabilities);
            System.out.println("Availability added");
            return new ResponseEntity<ApiResponse> (new ApiResponse(true, "Availabilities added"), HttpStatus.CREATED);
    
        } catch (Exception e) {
            System.out.println("Failed adding Availability:    " + e.getMessage());
            return new ResponseEntity<ApiResponse> (new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/getAllDoctors")
    public ResponseEntity<?> getAllDoctors() {
        List<Doctor> doctorList= doctorService.getAllDoctors();
        System.out.println("Total Doctors: "+ doctorList.size());
        return new ResponseEntity<>(doctorList, HttpStatus.OK);
    }

    @GetMapping("/getDoctorDetail/{doctor_id}")
    public ResponseEntity<?> getDoctorDetail(@PathVariable Long doctor_id) {
        Optional<User> optional_doctor = doctorService.getDoctorById(doctor_id);

        if(optional_doctor.isPresent()) {
            User doctor = optional_doctor.get();
            return new ResponseEntity<>(doctor, HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse(false, "Doctor not found"), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getDoctorSlots/{doctor_id}")
    public ResponseEntity<?> getDoctorSlots(@PathVariable Long doctor_id) {
        List<AppointmentSchedule> schedules = appointmentScheduleRepo.findUpcomingAvailableSlots(LocalDate.now(), doctor_id);

        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }
}
