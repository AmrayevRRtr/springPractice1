package com.example.tsis.service;

import com.example.tsis.dto.CreateBookingRequestDTO;
import com.example.tsis.dto.UpdateBookingRequestDTO;
import com.example.tsis.exception.BookingConflictException;
import com.example.tsis.exception.NotFoundException;
import com.example.tsis.model.Booking;
import com.example.tsis.model.BookingStatus;
import com.example.tsis.repository.BookingRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ObjectProvider<BookingAuditor> auditorProvider;

    public BookingService(BookingRepository bookingRepository,
                          ObjectProvider<BookingAuditor> auditorProvider) {
        this.bookingRepository = bookingRepository;
        this.auditorProvider = auditorProvider;
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    public Booking create(CreateBookingRequestDTO request) {
        if (bookingRepository.existsOverlapping(
                request.getRoomId(), request.getStartTime(), request.getEndTime(), null)) {
            throw new BookingConflictException(
                    "Room " + request.getRoomId() + " is already booked for this time range");
        }

        Booking booking = new Booking(
                null,
                request.getRoomId(),
                request.getGuestName(),
                request.getStartTime(),
                request.getEndTime(),
                BookingStatus.CONFIRMED
        );
        Booking saved = bookingRepository.save(booking);
        auditorProvider.ifAvailable(auditor -> auditor.recordCreated(saved));
        return saved;
    }

    public Booking update(Long id, UpdateBookingRequestDTO request) {
        Booking booking = getById(id);

        if (bookingRepository.existsOverlapping(
                request.getRoomId(), request.getStartTime(), request.getEndTime(), id)) {
            throw new BookingConflictException(
                    "Room " + request.getRoomId() + " is already booked for this time range");
        }

        booking.setRoomId(request.getRoomId());
        booking.setGuestName(request.getGuestName());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        Booking saved = bookingRepository.save(booking);
        auditorProvider.ifAvailable(auditor -> auditor.recordUpdated(saved));
        return saved;
    }

    public void delete(Long id) {
        if (!bookingRepository.deleteById(id)) {
            throw new NotFoundException("Booking with id " + id + " not found");
        }
        auditorProvider.ifAvailable(auditor -> auditor.recordDeleted(id));
    }
}