package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ultimate.wellme.models.Doctor;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Integer> {
    Doctor findByFirstName(String name);
}
