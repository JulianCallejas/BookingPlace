package com.rustik.rustik.repository;

import com.rustik.rustik.model.Booking;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {

    Optional<List<Booking>> findByCabinAndInitialDateLessThanEqualAndEndDateGreaterThanEqual (Cabin cabin, LocalDate initialDate, LocalDate endDate);

    Optional<List<Booking>> findByUser (User user);

    Optional<List<Booking>> findByCabin (Cabin cabin);

    Optional<List<Booking>> findByInitialDateLessThanEqualAndEndDateGreaterThanEqual (LocalDate initialDate, LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE :fechaBusqueda BETWEEN b.initialDate AND b.endDate")
    List<Booking> findBookingsWithDateInRange(LocalDate fechaBusqueda);


    @Query("SELECT b FROM Booking b WHERE b.cabin = :cabin " +
            "AND ((b.initialDate BETWEEN :initialDate AND :endDate) " +
            "OR (b.endDate BETWEEN :initialDate AND :endDate) " +
            "OR (b.initialDate <= :initialDate AND b.endDate >= :endDate))")
    Optional<List<Booking>> findExistingBookingsForCabin(@Param("cabin") Cabin cabin,
                                               @Param("initialDate") LocalDate initialDate,
                                               @Param("endDate") LocalDate endDate);
}
