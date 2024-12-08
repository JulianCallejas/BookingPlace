package com.rustik.rustik.controller;

import com.rustik.rustik.dto.BookingDTO;
import com.rustik.rustik.exception.BadRequestException;
import com.rustik.rustik.mapper.BookingMapper;
import com.rustik.rustik.model.Booking;
import com.rustik.rustik.model.BookingState;
import com.rustik.rustik.model.User;
import com.rustik.rustik.security.CustomUserDetails;
import com.rustik.rustik.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;



    @PostMapping ("/{id}")
    public ResponseEntity<BookingDTO> newBooking (@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id,
                                               @RequestBody @Valid BookingDTO bookingDTO){

        bookingDTO.setDates();
        Booking booking = bookingService.postBooking(
                userDetails.getUser(),
                id, bookingDTO

        );

        BookingDTO dto = BookingMapper.toDTO(booking);

        return ResponseEntity.ok(dto);

    }


    @GetMapping ("/my-bookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings (@AuthenticationPrincipal CustomUserDetails userDetails){


        List<BookingDTO> bookingsDTO = bookingService.findBookingByUser(userDetails.getUser()).stream().
                map(BookingMapper::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(bookingsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BookingDTO>> bookinsByCabin (@PathVariable Long id){

        List<BookingDTO> bookingsDTO = bookingService.findBookingByCabin(id).stream()
                .map(BookingMapper::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(bookingsDTO);
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> bookinsBydate (@RequestBody @Valid BookingDTO bookingDTO){

        List<BookingDTO> bookingsDTO = bookingService.findBookingByDates(bookingDTO.getInitialDate(),bookingDTO.getEndDate())
                .stream().map(BookingMapper::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(bookingsDTO);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingDTO>> allBooking (){

        List<BookingDTO> bookingsDTO = bookingService.findAllBookings()
                .stream().map(BookingMapper::toDTO).collect(Collectors.toList());

        return ResponseEntity.ok(bookingsDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelBooking (@PathVariable Long bookingId, @AuthenticationPrincipal CustomUserDetails userDetails){

        User user = userDetails.getUser();

        Booking solicitedBooking = bookingService.findBookingById(bookingId);

        if(solicitedBooking.getUser() == user && LocalDate.now().plusDays(2).isBefore(solicitedBooking.getInitialDate())){
            solicitedBooking.setState(BookingState.CANCELED);
            return ResponseEntity.ok(bookingService.cancelBooking(solicitedBooking));
        }
        throw new BadRequestException("No es posible cancelar esta resserva");
    }





}
