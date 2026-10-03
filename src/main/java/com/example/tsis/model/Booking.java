package com.example.tsis.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {

    private Long id;
    private Long roomId;
    private String guestName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;

    public Booking(Long id, Long roomId, String guestName,
                   LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.id = id;
        this.roomId = roomId;
        this.guestName = guestName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
