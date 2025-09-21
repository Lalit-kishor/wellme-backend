package com.ultimate.wellme.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tId;

    @ManyToOne
    @JoinColumn(name = "appointmentId")
    private Appointment appointment;

    private String status; 
    private double amount;
    private String date; // Consider using LocalDate for better date handling
    private String paymentMethod; // e.g., "Credit Card", "PayPal"
}
