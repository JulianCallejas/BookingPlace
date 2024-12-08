package com.rustik.rustik.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "cabin_id")
    @NonNull
    private Cabin cabin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NonNull
    private User user;


    @NonNull
    private LocalDate initialDate;

    @NonNull
    private LocalDate endDate;

    private Double totalPrice;


    private LocalDateTime createdAt;

    private BookingState state;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // Si el createdAt es nulo, lo asigna al momento actual.
        }
        if (this.state == null){
            this.state = BookingState.ACTIVE;
        }
    }



}


