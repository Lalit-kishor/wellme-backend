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
public class Patient extends User{

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private int age;
    private String gender;
    private int credits;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments = new ArrayList<>();
}
