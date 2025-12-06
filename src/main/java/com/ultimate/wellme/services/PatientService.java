package com.ultimate.wellme.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.PatientRepo;
import com.ultimate.wellme.models.Patient;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

    private BCryptPasswordEncoder passwordEncoder;

    public Patient savePatient(Patient patient){
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepo.save(patient);
    }

    public Patient getPatientById(Long patientId) {
        return patientRepo.findById(patientId);
    }
    
}
