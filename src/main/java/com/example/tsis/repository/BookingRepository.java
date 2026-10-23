package com.example.tsis.repository;

import com.example.tsis.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByRoomId(Long roomId, Pageable pageable);

    @Query("""
            select count(b) > 0 from Booking b
            where b.room.id = :roomId
              and b.status = com.example.tsis.model.BookingStatus.CONFIRMED
              and b.id <> :excludeId
              and :start < b.endTime
              and :end > b.startTime
            """)
    boolean existsOverlapping(@Param("roomId") Long roomId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end,
                              @Param("excludeId") Long excludeId);
}
