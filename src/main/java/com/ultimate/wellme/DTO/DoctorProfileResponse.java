package com.ultimate.wellme.DTO;

import java.util.List;

// record is a Java language construct(since Java 16).The compiler generates the constructor,accessors,equals(),hashCode(),and toString()for you,and the fields are implicitly

// final — so records are immutable by construction.

// So the honest answer is: a record is one way to implement a DTO

public record DoctorProfileResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    List<String> specializations,
    String medicalLicenseNumber,
    String clinicAddress,
    int yearsOfExperience,
    String gender,
    List<String> languages,
    int credits,
    int consultationFee,
    String city,
    String profileImageUrl
){}
