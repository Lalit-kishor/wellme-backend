package com.ultimate.wellme.mapper;

import com.ultimate.wellme.DTO.PatientProfileResponse;
import com.ultimate.wellme.DTO.PatientProfileUpdateRequest;
import com.ultimate.wellme.models.Patient;

public class PatientMapper {
    private PatientMapper() {}
 
    public static PatientProfileResponse toResponse(Patient patient) {
        return new PatientProfileResponse(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getEmail(),
            patient.getPhoneNumber(),
            patient.getAge(),
            patient.getGender(),
            patient.getBloodGroup(),
            patient.getAddress(),
            patient.getProfileImage() != null ? patient.getProfileImage().getImageUrl() : null
        );
    }
 
    public static void applyUpdate(Patient patient, PatientProfileUpdateRequest request) {
        if (request.firstName() != null) patient.setFirstName(request.firstName());
        if (request.lastName() != null) patient.setLastName(request.lastName());
        if (request.phoneNumber() != null) patient.setPhoneNumber(request.phoneNumber());
        if (request.age() != null) patient.setAge(request.age());
        if (request.gender() != null) patient.setGender(request.gender());
        if (request.bloodGroup() != null) patient.setBloodGroup(request.bloodGroup());
        if (request.address() != null) patient.setAddress(request.address());
    }
}
