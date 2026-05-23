package com.ultimate.wellme.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ultimate.wellme.models.DoctorAvailability;
import com.ultimate.wellme.models.User;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepo extends JpaRepository<DoctorAvailability, Long> {
    
    List<DoctorAvailability> findByDoctor(User doctor);
    
    List<DoctorAvailability> findByDoctorAndDay(User doctor, String day);

    List<DoctorAvailability> findByActiveTrue();
}