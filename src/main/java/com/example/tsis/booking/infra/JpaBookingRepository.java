package com.example.tsis.booking.infra;

import com.example.tsis.booking.domain.Booking;
import com.example.tsis.booking.domain.BookingRepository;
import com.example.tsis.room.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class JpaBookingRepository implements BookingRepository {

    private static final long NO_BOOKING = -1L;

    private final SpringDataBookingRepository jpa;
    private final RoomRepository roomRepository;
    private final BookingPersistenceMapper mapper;

    public JpaBookingRepository(SpringDataBookingRepository jpa,
                                RoomRepository roomRepository,
                                BookingPersistenceMapper mapper) {
        this.jpa = jpa;
        this.roomRepository = roomRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Booking> findAll(Long roomId, Pageable pageable) {
        Page<BookingEntity> page = (roomId == null)
                ? jpa.findAll(pageable)
                : jpa.findByRoomId(roomId, pageable);
        return page.map(mapper::toDomain);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Booking save(Booking booking) {
        var roomRef = roomRepository.getReferenceById(booking.getRoomId());
        return mapper.toDomain(jpa.save(mapper.toEntity(booking, roomRef)));
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsOverlapping(Long roomId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        return jpa.existsOverlapping(roomId, start, end,
                excludeBookingId == null ? NO_BOOKING : excludeBookingId);
    }
}