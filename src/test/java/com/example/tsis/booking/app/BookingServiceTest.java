package com.example.tsis.booking.app;

import com.example.tsis.booking.domain.*;
import com.example.tsis.common.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository repository;
    @Mock RoomLookup roomLookup;
    @InjectMocks BookingService service;

    final LocalDateTime start = LocalDateTime.of(2030, 5, 1, 10, 0);
    final LocalDateTime end = start.plusHours(2);

    BookingCommand cmd() { return new BookingCommand(1L, "John Doe", start, end); }

    @Test
    void create_savesConfirmedBooking_whenRoomExistsAndSlotIsFree() {
        when(roomLookup.exists(1L)).thenReturn(true);
        when(repository.existsOverlapping(1L, start, end, null)).thenReturn(false);
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = service.create(cmd());

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.getGuestName()).isEqualTo("John Doe");
        verify(repository).save(any(Booking.class));
    }

    @Test
    void create_throwsNotFound_whenRoomMissing() {
        when(roomLookup.exists(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.create(cmd())).isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void create_throwsConflict_whenSlotOverlaps() {
        when(roomLookup.exists(1L)).thenReturn(true);
        when(repository.existsOverlapping(1L, start, end, null)).thenReturn(true);

        assertThatThrownBy(() -> service.create(cmd())).isInstanceOf(BookingConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void create_throwsInvalid_whenEndBeforeStart() {
        var bad = new BookingCommand(1L, "John Doe", end, start);

        assertThatThrownBy(() -> service.create(bad)).isInstanceOf(InvalidBookingException.class);
        verifyNoInteractions(repository, roomLookup);
    }

    @Test
    void update_excludesItselfFromOverlapCheck_andChangesFields() {
        Booking existing = Booking.restore(5L, 1L, "Old Name", start, end, BookingStatus.CONFIRMED);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(roomLookup.exists(1L)).thenReturn(true);
        when(repository.existsOverlapping(1L, start, end, 5L)).thenReturn(false);
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = service.update(5L, cmd());

        assertThat(result.getGuestName()).isEqualTo("John Doe");
    }

    @Test
    void update_throwsConflict_whenBookingCancelled() {
        Booking cancelled = Booking.restore(5L, 1L, "X Y", start, end, BookingStatus.CANCELLED);
        when(repository.findById(5L)).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.update(5L, cmd())).isInstanceOf(BookingConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void cancel_setsStatusCancelled() {
        Booking existing = Booking.restore(5L, 1L, "X Y", start, end, BookingStatus.CONFIRMED);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.cancel(5L).getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}