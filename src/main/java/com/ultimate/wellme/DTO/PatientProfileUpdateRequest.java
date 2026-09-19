package com.ultimate.wellme.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientProfileUpdateRequest(
    @Size(max = 50) String firstName,
    @Size(max = 50) String lastName,
    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$") String phoneNumber,
    Integer age,
    String gender,
    String bloodGroup,
    @Size(max = 250) String address
) {}
