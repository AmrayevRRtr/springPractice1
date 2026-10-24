package com.example.tsis.booking.web;

import com.example.tsis.booking.app.BookingUseCase;
import com.example.tsis.booking.domain.Booking;
import com.example.tsis.common.web.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingUseCase bookingService;
    private final BookingWebMapper mapper;

    public BookingController(BookingUseCase bookingService, BookingWebMapper mapper) {
        this.bookingService = bookingService;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<BookingResponseDTO> getAll(
            @RequestParam(required = false) Long roomId,
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return PageResponse.from(bookingService.getAll(roomId, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    public BookingResponseDTO getById(@PathVariable Long id) {
        return mapper.toResponse(bookingService.getById(id));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody CreateBookingRequestDTO request) {
        Booking created = bookingService.create(mapper.toCommand(request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public BookingResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody UpdateBookingRequestDTO request) {
        return mapper.toResponse(bookingService.update(id, mapper.toCommand(request)));
    }

    @PostMapping("/{id}/cancel")
    public BookingResponseDTO cancel(@PathVariable Long id) {
        return mapper.toResponse(bookingService.cancel(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}