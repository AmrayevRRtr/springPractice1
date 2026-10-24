package com.example.tsis.booking.app;

import java.time.LocalDateTime;

public record BookingCommand(Long roomId, String guestName,
                             LocalDateTime startTime, LocalDateTime endTime) {
}