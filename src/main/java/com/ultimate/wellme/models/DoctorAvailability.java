package com.ultimate.wellme.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="doctor_availability", indexes = {
    @Index(name = "idx_doctor_active", columnList = "doctor_id, active"),
    @Index(name = "idx_day_active", columnList = "day, active"),
    @Index(name = "idx_doctor_day", columnList = "doctor_id, day")
})
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

    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 180, message = "Slot duration cannot exceed 3 hours")
    @Column(columnDefinition = "INT DEFAULT 30")
    private int slotDurationMinutes = 30;

    @NotNull
    @Min(value = 1, message = "At least 1 patient per slot required")
    @Max(value = 10, message = "Maximum 10 patients per slot allowed")
    @Column(columnDefinition = "INT DEFAULT 1")
    private int maxPatientsPerSlot = 1;

    private boolean active;   // variable name isActive is not  recommended ❌ because there is an issue with boolean fields naming convention in Java.

    @AssertTrue(message = "End time must be after start time")
    public boolean isValidTimeRange() {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }

    @Version
    private Long version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
