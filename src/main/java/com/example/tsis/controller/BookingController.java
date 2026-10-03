package com.example.tsis.controller;

import com.example.tsis.dto.BookingResponseDTO;
import com.example.tsis.dto.CreateBookingRequestDTO;
import com.example.tsis.dto.UpdateBookingRequestDTO;
import com.example.tsis.model.Booking;
import com.example.tsis.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingResponseDTO> getAll() {
        return bookingService.getAll().stream()
                .map(BookingResponseDTO::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getById(@PathVariable Long id) {
        return BookingResponseDTO.from(bookingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody CreateBookingRequestDTO request) {
        Booking created = bookingService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(BookingResponseDTO.from(created));
    }

    @PutMapping("/{id}")
    public BookingResponseDTO update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateBookingRequestDTO request) {
        return BookingResponseDTO.from(bookingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
