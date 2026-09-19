package com.ultimate.wellme.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.DTO.PatientProfileResponse;
import com.ultimate.wellme.DTO.PatientProfileUpdateRequest;
import com.ultimate.wellme.Repos.PatientRepo;
import com.ultimate.wellme.config.ResourceNotFoundException;
import com.ultimate.wellme.mapper.PatientMapper;
import com.ultimate.wellme.models.Patient;

import jakarta.transaction.Transactional;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public Patient savePatient(Patient patient){
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    public PatientProfileResponse getProfile(Long patientId) {
        Patient patient = findOrThrow(patientId);
        return PatientMapper.toResponse(patient);
    }
 
    @Transactional
    public PatientProfileResponse updateProfile(Long patientId, PatientProfileUpdateRequest request) {
        Patient patient = findOrThrow(patientId);
        PatientMapper.applyUpdate(patient, request);
        Patient saved = patientRepository.save(patient);
        return PatientMapper.toResponse(saved);
    }
 
    public Patient findOrThrow(Long patientId) {
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
    }
    
}
