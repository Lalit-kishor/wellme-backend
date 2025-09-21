package com.ultimate.wellme.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;
    private String status;

    @ManyToOne
    @JoinColumn(name = "patientId")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctorId")
    private User doctor;

    private String date;
    private String startTime;
    private String duration;
    private String type; // e.g., "In-Person" or "Virtual"

    @OneToOne
    @JoinColumn(name = "scheduleId", unique = true)
    private Schedule schedule;

    @OneToMany(mappedBy = "appointment")
    private List<Transaction> transactions = new ArrayList<>();
}
