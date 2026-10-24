package com.example.tsis.booking.web;

import com.example.tsis.booking.domain.BookingStatus;

import java.time.LocalDateTime;

public class BookingResponseDTO {

    private final Long id;
    private final Long roomId;
    private final String roomLabel;
    private final String guestName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BookingStatus bookingStatus;

    public BookingResponseDTO(Long id, Long roomId, String roomLabel, String guestName,
                              LocalDateTime startTime, LocalDateTime endTime,
                              BookingStatus bookingStatus) {
        this.id = id;
        this.roomId = roomId;
        this.roomLabel = roomLabel;
        this.guestName = guestName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomLabel() {
        return roomLabel;
    }

    public String getGuestName() {
        return guestName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }
}