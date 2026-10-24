package com.example.tsis.booking.app;

import com.example.tsis.booking.domain.Booking;
import com.example.tsis.config.AppProperties;
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

    public void recordCreated(Booking b) { log("created", b); }
    public void recordUpdated(Booking b) { log("updated", b); }
    public void recordCancelled(Booking b) { log("cancelled", b); }

    public void recordDeleted(Long id) {
        log.info("[{}] booking deleted: id={}", appProperties.environment(), id);
    }

    private void log(String action, Booking b) {
        log.info("[{}] booking {}: id={}, room={}, guest={}, {} - {}",
                appProperties.environment(), action,
                b.getId(), b.getRoomId(), b.getGuestName(), b.getStartTime(), b.getEndTime());
    }
}