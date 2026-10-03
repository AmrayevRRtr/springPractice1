package com.example.tsis.repository;

import com.example.tsis.model.Booking;
import com.example.tsis.model.BookingStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(sequence.incrementAndGet());
        }
        storage.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    @Override
    public boolean existsOverlapping(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        return storage.values().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .filter(b -> b.getRoomId().equals(roomId))
                .filter(b -> !Objects.equals(b.getId(), excludeId))
                .anyMatch(b -> start.isBefore(b.getEndTime()) && end.isAfter(b.getStartTime()));
    }
}
