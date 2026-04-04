package com.ultimate.wellme.models;

public enum AppointmentStatus {
    AVAILABLE("Available for booking"),
    PENDING("Pending for booking"),
    CONFIRMED("Booked by patient"),
    CANCELLED("Cancelled appointment"),
    BLOCKED("Blocked by doctor");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
