package com.example.tsis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class CreateBookingRequestDTO {

    @NotNull(message = "room id is required")
    @Positive(message = "room id must be positive")
    private Long roomId;

    @NotBlank
    private String guestName;

    @NotNull(message = "start time is required")
    @Future(message = "startTime must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "end time is required")
    private LocalDateTime endTime;

    public CreateBookingRequestDTO() {}

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

    @JsonIgnore
    @AssertTrue
    public boolean isTimeRangeValid() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }
}
