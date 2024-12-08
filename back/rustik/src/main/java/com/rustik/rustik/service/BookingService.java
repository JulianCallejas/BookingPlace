package com.rustik.rustik.service;

import com.rustik.rustik.dto.BookingDTO;
import com.rustik.rustik.exception.BadRequestException;
import com.rustik.rustik.mapper.CabinMapper;
import com.rustik.rustik.model.Booking;
import com.rustik.rustik.model.BookingState;
import com.rustik.rustik.model.Cabin;
import com.rustik.rustik.model.User;
import com.rustik.rustik.repository.BookingRepository;
import com.rustik.rustik.repository.CabinRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
            throw new BadRequestException("Precio incorrecto");
        }

        if (bookingDTO.getInitialDate().isBefore(LocalDate.now())){
            throw new BadRequestException("Fechas incorrectas, toda reserva debe ser posterior a " + LocalDate.now().toString());
        }

        if (bookingRepository.findExistingBookingsForCabin(cabin,bookingDTO.getInitialDate(),bookingDTO.getEndDate()).get().isEmpty()){
             Booking booking = new Booking();
             booking.setCabin(cabin);
             booking.setUser(user);
             booking.setInitialDate(bookingDTO.getInitialDate());
             booking.setEndDate(bookingDTO.getEndDate());
             booking.setTotalPrice(bookingDTO.getTotalPrice());

             return bookingRepository.save(booking);
        }

         throw new BadRequestException("La cabaña ya se encuentra reservada en esas fechas");


    }

    public List<Booking> findBookingByUser (User user) {

        List<Booking> bookings = bookingRepository.findByUser(user).orElseThrow();

        for (Booking booking : bookings) {
            if (booking.getInitialDate().isBefore(LocalDate.now())){
                booking.setState(BookingState.CONFIRMED);
                bookingRepository.save(booking);
            }

        }
        return bookings;
    }


    public List<Booking> findBookingByCabin (Long cabinId){
        Cabin cabin = cabinRepository.findById(cabinId).orElseThrow();

        List<Booking> bookings = bookingRepository.findByCabin(cabin).orElseThrow();

        for (Booking booking : bookings) {
            if (booking.getInitialDate().isBefore(LocalDate.now())){
                booking.setState(BookingState.CONFIRMED);
                bookingRepository.save(booking);
            }

        }
        return bookings;
    }

    public List<Booking> findBookingByDates (LocalDate initialDate, LocalDate endDate){
        List<Booking> bookings = bookingRepository.findByInitialDateLessThanEqualAndEndDateGreaterThanEqual(initialDate,endDate).orElseThrow();

        for (Booking booking : bookings) {
            if (booking.getInitialDate().isBefore(LocalDate.now())){
                booking.setState(BookingState.CONFIRMED);
                bookingRepository.save(booking);
            }

        }
        return bookings;
    }


    public Booking findBookingById (Long bookingId ){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();


        if (booking.getInitialDate().isBefore(LocalDate.now())){
            booking.setState(BookingState.CONFIRMED);
            bookingRepository.save(booking);
        }

        return booking;

    }


    public List<Booking> findAllBookings (){
        List<Booking> bookings = bookingRepository.findAll();

        for (Booking booking : bookings) {
            if (booking.getInitialDate().isBefore(LocalDate.now())){
                booking.setState(BookingState.CONFIRMED);
                bookingRepository.save(booking);
            }

        }
        return bookings;
    }

    public String cancelBooking (Booking booking){
        bookingRepository.save(booking);
        return "Booking canceled";
    }
}
