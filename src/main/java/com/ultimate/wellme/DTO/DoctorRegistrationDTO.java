package com.ultimate.wellme.DTO;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorRegistrationDTO {
    
    private String email;
    private String password;
    private String password_repeat;
    private String firstName;
    private String lastName;
    private String medicalLicenseNumber;
    private String specializations; // Comma-separated string
    private String clinicAddress;
    private int yearsOfExperience;
    private String gender;
    private String languages; // Comma-separated string
    private MultipartFile profilePicture;
}
