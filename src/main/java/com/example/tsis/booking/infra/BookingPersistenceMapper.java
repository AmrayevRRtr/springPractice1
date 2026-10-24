package com.example.tsis.booking.infra;

import com.example.tsis.booking.domain.Booking;
import com.example.tsis.room.Room;
import org.springframework.stereotype.Component;

@Component
public class BookingPersistenceMapper {

    public BookingEntity toEntity(Booking booking, Room roomRef) {
        return new BookingEntity(
                booking.getId(), roomRef, booking.getGuestName(),
                booking.getStartTime(), booking.getEndTime(), booking.getStatus());
    }

    public Booking toDomain(BookingEntity entity) {
        return Booking.restore(
                entity.getId(), entity.getRoom().getId(), entity.getGuestName(),
                entity.getStartTime(), entity.getEndTime(), entity.getStatus());
    }
}