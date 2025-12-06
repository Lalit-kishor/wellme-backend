package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ultimate.wellme.models.Patient;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Integer> {
    Patient findByFirstName(String name);

    Patient findById(Long patientId);
}
