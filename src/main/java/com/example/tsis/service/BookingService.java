package com.example.tsis.service;

import com.example.tsis.dto.CreateBookingRequestDTO;
import com.example.tsis.dto.UpdateBookingRequestDTO;
import com.example.tsis.exception.BookingConflictException;
import com.example.tsis.exception.NotFoundException;
import com.example.tsis.model.Booking;
import com.example.tsis.model.BookingStatus;
import com.example.tsis.model.Room;
import com.example.tsis.repository.BookingRepository;
import com.example.tsis.repository.RoomRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private static final long NO_BOOKING = -1L;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ObjectProvider<BookingAuditor> auditorProvider;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          ObjectProvider<BookingAuditor> auditorProvider) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.auditorProvider = auditorProvider;
    }

    @Transactional(readOnly = true)
    public Page<Booking> getAll(Long roomId, Pageable pageable) {
        if (roomId == null) {
            return bookingRepository.findAll(pageable);
        }
        return bookingRepository.findByRoomId(roomId, pageable);
    }

    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    @Transactional
    public Booking create(CreateBookingRequestDTO request) {
        Room room = getRoom(request.getRoomId());
        checkNoOverlap(room.getId(), request.getStartTime(), request.getEndTime(), NO_BOOKING);

        Booking booking = new Booking(
                room,
                request.getGuestName(),
                request.getStartTime(),
                request.getEndTime(),
                BookingStatus.CONFIRMED
        );
        Booking saved = bookingRepository.save(booking);
        auditorProvider.ifAvailable(auditor -> auditor.recordCreated(saved));
        return saved;
    }

    @Transactional
    public Booking update(Long id, UpdateBookingRequestDTO request) {
        Booking booking = getById(id);
        Room room = getRoom(request.getRoomId());
        checkNoOverlap(room.getId(), request.getStartTime(), request.getEndTime(), id);

        booking.setRoom(room);
        booking.setGuestName(request.getGuestName());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        Booking saved = bookingRepository.save(booking);
        auditorProvider.ifAvailable(auditor -> auditor.recordUpdated(saved));
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Booking booking = getById(id);
        bookingRepository.delete(booking);
        auditorProvider.ifAvailable(auditor -> auditor.recordDeleted(id));
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room with id " + roomId + " not found"));
    }

    private void checkNoOverlap(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeId) {
        if (bookingRepository.existsOverlapping(roomId, start, end, excludeId)) {
            throw new BookingConflictException(
                    "Room " + roomId + " is already booked for this time range");
        }
    }
}