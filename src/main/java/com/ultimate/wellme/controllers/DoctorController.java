package com.ultimate.wellme.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ultimate.wellme.DTO.DoctorAvailabilityDTO;
import com.ultimate.wellme.config.ApiResponse;
import com.ultimate.wellme.models.DoctorAvailability;
import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.DoctorAvailabilityService;
import com.ultimate.wellme.services.DoctorService;
import com.ultimate.wellme.services.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DoctorController {

    @Autowired
    JwtService jwtService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorAvailabilityService doctorAvailabilityService;
    
    @PostMapping("/addAvailability")
    public ResponseEntity<ApiResponse> addAvailability(@RequestBody List<DoctorAvailabilityDTO> doctorAvailabilityDTO, HttpServletRequest request) {

        try {
            System.out.println("Hello /addAvailability");
    
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


            Optional<User> doctor = doctorService.getDoctorById(doctorId);

            if(!doctor.isPresent()) return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Doctor Not found"),
                        HttpStatus.BAD_REQUEST);

            // 👉🏽 String role = authentication.getAuthorities().iterator().next().getAuthority();
            
            List<DoctorAvailability> availabilities = new ArrayList<>();

            for(DoctorAvailabilityDTO  dto: doctorAvailabilityDTO) {
                DoctorAvailability doctorAvailability = new DoctorAvailability();
                doctorAvailability.setDay(dto.getDay());
                doctorAvailability.setStartTime(dto.getStartTime());
                doctorAvailability.setEndTime(dto.getEndTime());
                doctorAvailability.setSlotDurationMinutes(dto.getSlotDurationMinutes());
                doctorAvailability.setMaxPatientsPerSlot(dto.getMaxPatientsPerSlot());
                doctorAvailability.setActive(dto.isActive());
                doctorAvailability.setDoctor(doctor.get());

                availabilities.add(doctorAvailability);
            }
           

            doctorAvailabilityService.saveAll(availabilities);
            System.out.println("Availability added");
            return new ResponseEntity<ApiResponse> (new ApiResponse(true, "Availabilities added"), HttpStatus.CREATED);
    
        } catch (Exception e) {
            System.out.println("Failed adding Availability");
            return new ResponseEntity<ApiResponse> (new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }
}
