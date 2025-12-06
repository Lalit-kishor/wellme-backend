package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ultimate.wellme.models.Appointment;

@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    
}
