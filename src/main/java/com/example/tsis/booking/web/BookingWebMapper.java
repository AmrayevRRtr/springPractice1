package com.example.tsis.booking.web;

import com.example.tsis.booking.app.BookingCommand;
import com.example.tsis.booking.domain.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingWebMapper {

    public BookingCommand toCommand(CreateBookingRequestDTO r) {
        return new BookingCommand(r.getRoomId(), r.getGuestName(), r.getStartTime(), r.getEndTime());
    }

    public BookingCommand toCommand(UpdateBookingRequestDTO r) {
        return new BookingCommand(r.getRoomId(), r.getGuestName(), r.getStartTime(), r.getEndTime());
    }

    public BookingResponseDTO toResponse(Booking b) {
        return new BookingResponseDTO(
                b.getId(),
                b.getRoomId(),
                "Room #" + b.getRoomId(),
                b.getGuestName(),
                b.getStartTime(),
                b.getEndTime(),
                b.getStatus());
    }
}