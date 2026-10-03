package com.example.tsis.repository;

import com.example.tsis.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    boolean deleteById(Long id);

    boolean existsOverlapping(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeId);
}
