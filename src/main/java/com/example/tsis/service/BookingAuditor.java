package com.example.tsis.service;

import com.example.tsis.config.AppProperties;
import com.example.tsis.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.booking-audit", name = "enabled", havingValue = "true")
public class BookingAuditor {

    private static final Logger log = LoggerFactory.getLogger(BookingAuditor.class);

    private final AppProperties appProperties;

    public BookingAuditor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void recordCreated(Booking booking) {
        log.info("[{}] booking created: id={}, room={}, guest={}, {} - {}",
                appProperties.environment(),
                booking.getId(), booking.getRoomId(), booking.getGuestName(),
                booking.getStartTime(), booking.getEndTime());
    }

    public void recordUpdated(Booking booking) {
        log.info("[{}] booking updated: id={}, room={}, guest={}, {} - {}",
                appProperties.environment(),
                booking.getId(), booking.getRoomId(), booking.getGuestName(),
                booking.getStartTime(), booking.getEndTime());
    }

    public void recordDeleted(Long id) {
        log.info("[{}] booking deleted: id={}", appProperties.environment(), id);
    }
}
