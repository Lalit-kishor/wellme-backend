package com.ultimate.wellme.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ultimate.wellme.Repos.UserRepo;
import com.ultimate.wellme.models.Doctor;
import com.ultimate.wellme.models.User;

@Service
public class DoctorService {
    
    @Autowired
    private UserRepo userRepo;

    public List<Doctor> getAllDoctors() {
        // Fetch only users with DOCTOR Role

        return userRepo.findByRole(User.Role.DOCTOR).stream().map(user -> (Doctor) user).collect(Collectors.toList());
    }

    public Optional<User> getDoctorById(Long doctorId) {
        return userRepo.findById(doctorId);
    }
}
