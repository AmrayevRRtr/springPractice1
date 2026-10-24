package com.example.tsis.booking.infra;

import com.example.tsis.booking.domain.Booking;
import com.example.tsis.booking.domain.BookingStatus;
import com.example.tsis.room.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import({JpaBookingRepository.class, BookingPersistenceMapper.class})
class BookingPersistenceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired JpaBookingRepository repository;
    @Autowired RoomRepository roomRepository;

    Long alphaId() { return roomRepository.findByName("Alpha").orElseThrow().getId(); }
    Long betaId() { return roomRepository.findByName("Beta").orElseThrow().getId(); }

    @Test
    void saveAndFindById_roundTrip() {
        Booking saved = repository.save(Booking.create(alphaId(), "Test Guest",
                LocalDateTime.of(2031, 3, 1, 10, 0), LocalDateTime.of(2031, 3, 1, 11, 0)));

        assertThat(saved.getId()).isNotNull();
        Booking loaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getGuestName()).isEqualTo("Test Guest");
        assertThat(loaded.getRoomId()).isEqualTo(alphaId());
        assertThat(loaded.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void existsOverlapping_detectsConflict_againstSeedData() {
        // seed (V2): Alpha занята 2030-02-01 10:00-12:00
        LocalDateTime d = LocalDateTime.of(2030, 2, 1, 0, 0);

        assertThat(repository.existsOverlapping(alphaId(), d.withHour(11), d.withHour(13), null)).isTrue();
        assertThat(repository.existsOverlapping(alphaId(), d.withHour(12), d.withHour(13), null)).isFalse(); // впритык
        assertThat(repository.existsOverlapping(betaId(), d.withHour(10), d.withHour(12), null)).isFalse(); // другая комната
    }

    @Test
    void existsOverlapping_ignoresCancelledAndExcludedBooking() {
        Booking saved = repository.save(Booking.create(alphaId(), "Test Guest",
                LocalDateTime.of(2031, 3, 1, 10, 0), LocalDateTime.of(2031, 3, 1, 11, 0)));
        LocalDateTime s = saved.getStartTime(), e = saved.getEndTime();

        assertThat(repository.existsOverlapping(alphaId(), s, e, null)).isTrue();
        assertThat(repository.existsOverlapping(alphaId(), s, e, saved.getId())).isFalse(); // исключили саму себя

        saved.cancel();
        repository.save(saved);
        assertThat(repository.existsOverlapping(alphaId(), s, e, null)).isFalse();          // отменённые не блокируют
    }

    @Test
    void findAll_filtersByRoom_andPages() {
        Page<Booking> page = repository.findAll(alphaId(), PageRequest.of(0, 10, Sort.by("startTime")));

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent()).allMatch(b -> b.getRoomId().equals(alphaId()));
        assertThat(page.getContent()).extracting(Booking::getGuestName).contains("Seed Guest One");
    }
}