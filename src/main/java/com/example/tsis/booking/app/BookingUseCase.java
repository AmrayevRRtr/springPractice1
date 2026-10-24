package com.example.tsis.booking.app;

import com.example.tsis.booking.domain.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingUseCase {

    Page<Booking> getAll(Long roomId, Pageable pageable);

    Booking getById(Long id);

    Booking create(BookingCommand cmd);

    Booking update(Long id, BookingCommand cmd);

    Booking cancel(Long id);

    void delete(Long id);
}
