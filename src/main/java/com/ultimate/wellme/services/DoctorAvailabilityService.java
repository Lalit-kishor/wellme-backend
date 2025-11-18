package com.ultimate.wellme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.DoctorAvailabilityRepo;
import com.ultimate.wellme.models.DoctorAvailability;

@Service
public class DoctorAvailabilityService {
    
    @Autowired
    private DoctorAvailabilityRepo doctorAvailabilityRepo;

    public DoctorAvailability saveDoctorAvailability(DoctorAvailability doctorAvailability) {

        return doctorAvailabilityRepo.save(doctorAvailability);
    }

    public List<DoctorAvailability> saveAll(List<DoctorAvailability> availabilities) {
        return doctorAvailabilityRepo.saveAll(availabilities);
    }
}
