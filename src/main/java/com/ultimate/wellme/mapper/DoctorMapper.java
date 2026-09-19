package com.ultimate.wellme.mapper;

import com.ultimate.wellme.DTO.DoctorProfileResponse;
import com.ultimate.wellme.DTO.DoctorProfileUpdateRequest;
import com.ultimate.wellme.models.Doctor;

/**
 * Manual mapper — no extra dependency needed. If this grows past a couple of
 * entities, MapStruct (mapstruct.org) is worth adding: it generates this exact
 * kind of code at compile time (no reflection cost) and keeps mappers in sync
 * with entity changes automatically. Free, widely used in Spring Boot shops,
 * and a nice thing to point to in interviews.
 */

public class DoctorMapper {

    private DoctorMapper() {}
 
    public static DoctorProfileResponse toResponse(Doctor doctor) {
        return new DoctorProfileResponse(
            doctor.getId(),
            doctor.getFirstName(),
            doctor.getLastName(),
            doctor.getEmail(),
            doctor.getSpecializations(),
            doctor.getMedicalLicenseNumber(),
            doctor.getClinicAddress(),
            doctor.getYearsOfExperience(),
            doctor.getGender(),
            doctor.getLanguages(),
            doctor.getCredits(),
            doctor.getConsultationFee(),
            doctor.getCity(),
            
            doctor.getProfileImage() != null ? doctor.getProfileImage().getImageUrl() : null
        );
    }
 
    /** Applies only the non-null fields present in the request onto the managed entity. */
    public static void applyUpdate(Doctor doctor, DoctorProfileUpdateRequest request) {
        if (request.firstName() != null) doctor.setFirstName(request.firstName());
        if (request.lastName() != null) doctor.setLastName(request.lastName());
        if (request.specializations() != null) doctor.setSpecializations(request.specializations());
        if (request.yearsOfExperience() != null) doctor.setYearsOfExperience(request.yearsOfExperience());
        if (request.gender() != null) doctor.setGender(request.gender());
        if (request.languages() != null) doctor.setLanguages(request.languages());
        if (request.clinicAddress() != null) doctor.setClinicAddress(request.clinicAddress());
        if (request.city() != null) doctor.setCity(request.city());
        if (request.consultationFee() != null) doctor.setConsultationFee(request.consultationFee());
    }
    
}
