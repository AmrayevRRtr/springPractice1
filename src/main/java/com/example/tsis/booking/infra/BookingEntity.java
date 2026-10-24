package com.example.tsis.booking.infra;

import com.example.tsis.booking.domain.BookingStatus;
import com.example.tsis.room.Room;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "guest_name", nullable = false, length = 50)
    private String guestName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    protected BookingEntity() {
    }

    public BookingEntity(Long id, Room room, String guestName,
                         LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.id = id;
        this.room = room;
        this.guestName = guestName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getId() { return id; }
    public Room getRoom() { return room; }
    public String getGuestName() { return guestName; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BookingStatus getStatus() { return status; }
}