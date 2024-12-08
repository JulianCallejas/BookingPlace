package com.rustik.rustik.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rustik.rustik.model.BookingState;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookingDTO {

    private Long id;

    private CabinDTO cabin;

    private UserDTO user;

    @NotNull
    private LocalDate initialDate;

    @NotNull
    private LocalDate endDate;


    private List<LocalDate> dates = new ArrayList<>();

    private Double totalPrice;

    @JsonIgnore
    private LocalDateTime createdAt;

    private BookingState state;


    public void setDates (){
        LocalDate currentDate = this.initialDate;

        while (!currentDate.isAfter(endDate)){
            this.dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

    }

    public void setTotalPrice (){
        this.totalPrice = (cabin.getPrice() * dates.size());
    }





}
