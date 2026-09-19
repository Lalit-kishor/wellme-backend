package com.ultimate.wellme.DTO;

public record PatientProfileResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Integer age,
    String gender,
    String bloodGroup,
    String address,
    String profileImageUrl
) {}
