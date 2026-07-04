package com.ultimate.wellme.DTO;

import java.util.List;

public class DoctorAvailabilityDetails {

    private List<DoctorAvailabilityDTO> availabilities;

    public List<DoctorAvailabilityDTO> getAvailability() {
        return availabilities;
    }

    public void setAvailability(List<DoctorAvailabilityDTO> availability) {
        this.availabilities = availability;
    }
}
