package com.example.tsis.booking.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository {
    Page<Booking> findAll(Long roomId, Pageable pageable);
    Optional<Booking> findById(Long id);
    Booking save(Booking booking);
    void deleteById(Long id);
    boolean existsOverlapping(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeBookingId);
}