package com.example.tsis.booking.app;

import com.example.tsis.booking.domain.Booking;
import com.example.tsis.booking.domain.BookingConflictException;
import com.example.tsis.booking.domain.BookingRepository;
import com.example.tsis.booking.domain.RoomLookup;
import com.example.tsis.common.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingService implements BookingUseCase {

    public static final String BOOKING_CACHE = "bookings";

    private final BookingRepository repository;
    private final RoomLookup roomLookup;
    private final BookingAuditor auditor;

    public BookingService(BookingRepository repository,
                          RoomLookup roomLookup,
                          @Autowired(required = false) BookingAuditor auditor) {
        this.repository = repository;
        this.roomLookup = roomLookup;
        this.auditor = auditor;
    }

    @Transactional(readOnly = true)
    public Page<Booking> getAll(Long roomId, Pageable pageable) {
        return repository.findAll(roomId, pageable);
    }

    @Cacheable(cacheNames = BOOKING_CACHE, key = "#id")
    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return findOrThrow(id);
    }

    @Transactional
    public Booking create(BookingCommand cmd) {
        Booking booking = Booking.create(cmd.roomId(), cmd.guestName(), cmd.startTime(), cmd.endTime());
        requireRoom(cmd.roomId());
        checkNoOverlap(cmd.roomId(), cmd.startTime(), cmd.endTime(), null);

        Booking saved = repository.save(booking);
        if (auditor != null) {
            auditor.recordCreated(saved);
        }
        return saved;
    }

    @CacheEvict(cacheNames = BOOKING_CACHE, key = "#id")
    @Transactional
    public Booking update(Long id, BookingCommand cmd) {
        Booking booking = findOrThrow(id);
        booking.reschedule(cmd.roomId(), cmd.guestName(), cmd.startTime(), cmd.endTime());
        requireRoom(cmd.roomId());
        checkNoOverlap(cmd.roomId(), cmd.startTime(), cmd.endTime(), id);

        Booking saved = repository.save(booking);
        if (auditor != null) {
            auditor.recordUpdated(saved);
        }
        return saved;
    }

    @CacheEvict(cacheNames = BOOKING_CACHE, key = "#id")
    @Transactional
    public Booking cancel(Long id) {
        Booking booking = findOrThrow(id);
        booking.cancel();
        Booking saved = repository.save(booking);
        if (auditor != null) {
            auditor.recordCancelled(saved);
        }
        return saved;
    }

    @CacheEvict(cacheNames = BOOKING_CACHE, key = "#id")
    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
        if (auditor != null) {
            auditor.recordDeleted(id);
        }
    }

    private Booking findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    private void requireRoom(Long roomId) {
        if (!roomLookup.exists(roomId)) {
            throw new NotFoundException("Room with id " + roomId + " not found");
        }
    }

    private void checkNoOverlap(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        if (repository.existsOverlapping(roomId, start, end, excludeId)) {
            throw new BookingConflictException("Room " + roomId + " is already booked for this time range");
        }
    }
}