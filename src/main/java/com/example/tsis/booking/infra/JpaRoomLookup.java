package com.example.tsis.booking.infra;

import com.example.tsis.booking.domain.RoomLookup;
import com.example.tsis.room.RoomRepository;
import org.springframework.stereotype.Component;

@Component
public class JpaRoomLookup implements RoomLookup {

    private final RoomRepository roomRepository;

    public JpaRoomLookup(RoomRepository roomRepository) { this.roomRepository = roomRepository; }

    @Override
    public boolean exists(Long roomId) { return roomRepository.existsById(roomId); }
}