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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "appointments", indexes = {
        @Index(name = "idx_patient_date", columnList = "patientId, date"),
        @Index(name = "idx_doctor_date", columnList = "doctorId, date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_date_status", columnList = "date, status")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW, RESCHEDULED
    }

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private int consultationFee;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "patientId")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctorId")
    private User doctor;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime startTime;

    private String type; // e.g., "In-Person" or "Virtual"
    private String notes;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private AppointmentSchedule appointmentSchedule;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.EAGER)
    private Transaction transaction;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted=false;  // soft delete

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @AssertTrue(message = "Appointment date cannot be in the past")
    public boolean isValidDate() {
        return date == null || !date.isBefore(LocalDate.now());
    }
}
