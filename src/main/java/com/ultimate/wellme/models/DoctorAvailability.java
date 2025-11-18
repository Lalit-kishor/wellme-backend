package com.ultimate.wellme.models;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DoctorAvailability {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    /*
     * The above foreign key will not be auto populated in the table.
     * JPA only fills the FK when you set the doctor object before saving.
     * 
     * If you don’t set it → JPA cannot know the doctor → DB will reject (nullable =false).
     */
    
    private String day;
    private LocalTime startTime;
    private LocalTime endTime;
    private int slotDurationMinutes;
    private int maxPatientsPerSlot;
    private boolean active;   // variable name isActive is not  recommended ❌ because there is an issue with boolean fields naming convention in Java.
}
