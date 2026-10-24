package com.example.tsis.booking.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SpringDataBookingRepository extends JpaRepository<BookingEntity, Long> {

    Page<BookingEntity> findByRoomId(Long roomId, Pageable pageable);

    @Query("""
            select count(b) > 0 from BookingEntity b
            where b.room.id = :roomId
              and b.status = com.example.tsis.booking.domain.BookingStatus.CONFIRMED
              and b.id <> :excludeId
              and :start < b.endTime
              and :end > b.startTime
            """)
    boolean existsOverlapping(@Param("roomId") Long roomId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("excludeId") Long excludeId);
}