package com.ultimate.wellme.DTO;

import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * All fields are nullable by design — this is a PATCH body, not a PUT.
 * Only non-null fields are applied to the entity (see
 * DoctorMapper.applyUpdate).
 *
 * Deliberately excludes:
 * - email / password / role → these should go through dedicated, more guarded
 * flows
 * (email change verification, password reset, admin-only role change)
 * - medicalLicenseNumber → should require re-verification, not be self-service
 * editable
 */
public record DoctorProfileUpdateRequest(
        @Size(max = 50) String firstName,
        @Size(max = 50) String lastName,
        List<String> specializations,
        Integer yearsOfExperience,
        String gender,
        List<String> languages,
        @Size(max = 200) String clinicAddress,
        @Size(max = 100) String city,
        @DecimalMin(value = "0.0", inclusive = true) Integer consultationFee) {
}
