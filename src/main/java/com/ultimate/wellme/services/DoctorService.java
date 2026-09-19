package com.ultimate.wellme.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.DTO.DoctorProfileResponse;
import com.ultimate.wellme.DTO.DoctorProfileUpdateRequest;
import com.ultimate.wellme.DTO.SlotResponse;
import com.ultimate.wellme.Repos.AppointmentScheduleRepo;
import com.ultimate.wellme.Repos.DoctorRepo;
import com.ultimate.wellme.Repos.UserRepo;
import com.ultimate.wellme.config.ResourceNotFoundException;
import com.ultimate.wellme.mapper.DoctorMapper;
import com.ultimate.wellme.mapper.SlotMapper;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.User;

import jakarta.transaction.Transactional;

@Service
public class DoctorService {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DoctorRepo doctorRepository;

    @Autowired 
    private AppointmentScheduleRepo appointmentScheduleRepo;

    public List<Doctor> getAllDoctors() {
        // Fetch only users with DOCTOR Role

        return userRepo.findByRole(User.Role.DOCTOR).stream().map(user -> (Doctor) user).collect(Collectors.toList());
    }

    public Optional<User> getDoctorById(Long doctorId) {
        return userRepo.findById(doctorId);
    }

    public List<SlotResponse> getUpcomingSlots(Long id) {
        // check if this id is valid
        findOrThrow(id);
        return appointmentScheduleRepo.findUpcomingAvailableSlots(LocalDate.now(), id).stream().map(SlotMapper::toResponse).toList();
    }

    public DoctorProfileResponse getProfile(Long doctor_id) {
        Doctor doctor = findOrThrow(doctor_id);
        return DoctorMapper.toResponse(doctor);
    }

    @Transactional
    public DoctorProfileResponse updateProfile(Long doctorId, DoctorProfileUpdateRequest request) {
        Doctor doctor = findOrThrow(doctorId);
        DoctorMapper.applyUpdate(doctor, request);
        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toResponse(saved);
    }

    private Doctor findOrThrow(Long doctor_id) {
        return doctorRepository.findById(doctor_id).orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctor_id));
    }

    
}
