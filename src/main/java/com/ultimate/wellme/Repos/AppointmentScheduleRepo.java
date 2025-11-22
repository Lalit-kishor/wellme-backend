package com.ultimate.wellme.Repos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ultimate.wellme.models.AppointmentSchedule;
import com.ultimate.wellme.models.AppointmentStatus;
import com.ultimate.wellme.models.User;

@Repository
public interface AppointmentScheduleRepo extends JpaRepository<AppointmentSchedule, Long>{

    // Check if slots already exist for a doctor on a specific date
    boolean existsByDoctorAndDate(User doctor, LocalDate date);

    // Find available slots for a doctor on a specific date
    List<AppointmentSchedule> findByDoctorAndDateBetween(User doctor, LocalDate startDate, LocalDate endDate);
    
    // Find slots by status within date range (for analytics)
    @Query("select a from AppointmentSchedule a where a.status= :status and a.date between :startDate and :endDate and a.isDeleted=false")
    List<AppointmentSchedule> findByStatusAndDateRange(@Param("status") AppointmentStatus status,
                                                                                                    @Param("startDate") LocalDate startDate,
                                                                                                @Param("endDate") LocalDate endDate);

    
    // Find expired slots that need cleanup
    @Query("select a from AppointmentSchedule a where a.status= :status and a.date < :currentDate and a.isDeleted=false")
    List<AppointmentSchedule> findExpiredAvailableSlots(@Param("currentDate") LocalDate currentDate, @Param("status") AppointmentStatus status);

    // Performance query: Count slots by doctor and status
    @Query("SELECT COUNT(a) FROM AppointmentSchedule a WHERE a.doctor = :doctor AND a.status = :status AND a.isDeleted = false")
    long countByDoctorAndStatus(@Param("doctor") User doctor, @Param("status") AppointmentStatus status);
}
