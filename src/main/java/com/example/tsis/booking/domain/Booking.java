package com.example.tsis.booking.domain;

import java.time.LocalDateTime;

public class Booking {

    private Long id;
    private Long roomId;
    private String guestName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    private Booking(Long id, Long roomId, String guestName,
                    LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.guestName = guestName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public static Booking create(Long roomId, String guestName,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        validate(roomId, guestName, startTime, endTime);
        return new Booking(null, roomId, guestName, startTime, endTime, BookingStatus.CONFIRMED);
    }

    public static Booking restore(Long id, Long roomId, String guestName,
                                  LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        return new Booking(id, roomId, guestName, startTime, endTime, status);
    }

    public void reschedule(Long roomId, String guestName,
                           LocalDateTime startTime, LocalDateTime endTime) {
        if (status == BookingStatus.CANCELLED) {
            throw new BookingConflictException("Cancelled booking cannot be modified");
        }
        validate(roomId, guestName, startTime, endTime);
        this.roomId = roomId;
        this.guestName = guestName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new BookingConflictException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }

    private static void validate(Long roomId, String guestName,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        if (roomId == null) throw new InvalidBookingException("roomId is required");
        if (guestName == null || guestName.isBlank() || guestName.length() > 50)
            throw new InvalidBookingException("guestName must be 1 to 50 characters");
        if (startTime == null || endTime == null)
            throw new InvalidBookingException("startTime and endTime are required");
        if (!endTime.isAfter(startTime))
            throw new InvalidBookingException("endTime must be after startTime");
    }

    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public String getGuestName() { return guestName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BookingStatus getStatus() { return status; }
}