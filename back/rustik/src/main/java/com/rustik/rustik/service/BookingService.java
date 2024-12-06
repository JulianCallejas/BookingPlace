package com.rustik.rustik.service;

import com.rustik.rustik.dto.BookingDTO;
import com.rustik.rustik.exception.BadRequestException;
import com.rustik.rustik.mapper.CabinMapper;
import com.rustik.rustik.model.Booking;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.User;
import com.rustik.rustik.repository.BookingRepository;
import com.rustik.rustik.repository.CabinRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private UserService userService;

    @Autowired
    private CabinRepository cabinRepository;

    @Autowired
    private BookingRepository bookingRepository;


    public static final Logger logger = Logger.getLogger(BookingService.class);


    public Booking postBooking (User user, Long cabinId, BookingDTO bookingDTO){

        Cabin cabin = cabinRepository.findById(cabinId).get();
        Double price = bookingDTO.getTotalPrice();

        logger.info(price.toString());
        bookingDTO.setCabin(CabinMapper.toDTO(cabin));
        bookingDTO.setTotalPrice();
        logger.info(bookingDTO.getTotalPrice().toString());

        if ( price.doubleValue() != bookingDTO.getTotalPrice().doubleValue()){
            throw new BadRequestException("precio incorrecto");
        }

        logger.warn(bookingRepository.findExistingBookingsForCabin(cabin,bookingDTO.getInitialDate(),bookingDTO.getEndDate()).isPresent());
        if (bookingRepository.findExistingBookingsForCabin(cabin,bookingDTO.getInitialDate(),bookingDTO.getEndDate()).get().isEmpty() ){
             Booking booking = new Booking();
             booking.setCabin(cabin);
             booking.setUser(user);
             booking.setInitialDate(bookingDTO.getInitialDate());
             booking.setEndDate(bookingDTO.getEndDate());
             //booking.setBookingDate(LocalDate.now());

             return bookingRepository.save(booking);
        }

         throw new BadRequestException("LA cabaña ya se encuentra reservada en esas fechas");


    }

    public List<Booking> findBookingByUser (User user) {
        return bookingRepository.findByUser(user).orElseThrow();
    }


    public List<Booking> findBookingByCabin (Long cabinId){
        Cabin cabin = cabinRepository.findById(cabinId).orElseThrow();

        return bookingRepository.findByCabin(cabin).orElseThrow();
    }

    public List<Booking> findBookingByDates (LocalDate initialDate, LocalDate endDate){
        return bookingRepository.findByInitialDateLessThanEqualAndEndDateGreaterThanEqual(initialDate,endDate).orElseThrow();
    }


    public List<Booking> findByCabinAndDates (Long cabinId, LocalDate initialDate, LocalDate endDate ){
        Cabin cabin = cabinRepository.findById(cabinId).orElseThrow();
        return bookingRepository.findByCabinAndInitialDateLessThanEqualAndEndDateGreaterThanEqual(cabin,initialDate,endDate).orElseThrow();

    }

}
