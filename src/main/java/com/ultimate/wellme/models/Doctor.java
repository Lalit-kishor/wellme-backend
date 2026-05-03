package com.ultimate.wellme.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Doctor extends User{

    private String firstName;
    private String lastName;
    private String medicalLicenseNumber;
    private List<String> specializations;
    private String clinicAddress;
    private int yearsOfExperience;
    private String gender;
    private List<String> languages;
    private int credits;
    private int consultationFee;
    private String city;


    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments = new ArrayList<>();
}
