package com.ultimate.wellme.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "appointment_schedules", indexes = {
    @Index(name="idx_doctor_date", columnList = "doctor_id, date"),
    @Index(name="idx_status", columnList = "status"),
    @Index(name="idx_date_time", columnList = "date, start_time"),
    @Index(name="idx_doctor_status", columnList = "doctor_id, status")
})
public class AppointmentSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Version
    private Long version;   // Optimistic locking; Prevents concurrent booking conflicts

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.AVAILABLE;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // Soft delete
    private Boolean isDeleted = false;

    // Business fields
    private String patientId;  // when booked
    private String notes;  // Additional info

    // Audit Methods
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt  =  LocalDateTime.now();
    }

}
