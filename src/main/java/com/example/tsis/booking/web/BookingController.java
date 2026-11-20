package com.example.tsis.booking.web;

import com.example.tsis.booking.app.BookingUseCase;
import com.example.tsis.booking.domain.Booking;
import com.example.tsis.common.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Bookings", description = "Create, read, update, cancel and delete room bookings")
public class BookingController {

    private final BookingUseCase bookingService;
    private final BookingWebMapper mapper;

    public BookingController(BookingUseCase bookingService, BookingWebMapper mapper) {
        this.bookingService = bookingService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "List bookings",
            description = "Paged list, optionally filtered by room. Supports page, size and sort=field,direction.")
    @ApiResponse(responseCode = "200", description = "Page of bookings")
    @ApiResponse(responseCode = "400", description = "Unknown sort property",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public PageResponse<BookingResponseDTO> getAll(
            @Parameter(description = "Only bookings of this room", example = "1")
            @RequestParam(required = false) Long roomId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable) {
        return PageResponse.from(bookingService.getAll(roomId, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking by id")
    @ApiResponse(responseCode = "200", description = "Booking found")
    @ApiResponse(responseCode = "404", description = "Booking does not exist",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public BookingResponseDTO getById(@Parameter(description = "Booking id", example = "3") @PathVariable Long id) {
        return mapper.toResponse(bookingService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a booking",
            description = "Returns 201 with a Location header pointing to the new booking.")
    @ApiResponse(responseCode = "201", description = "Booking created")
    @ApiResponse(responseCode = "400", description = "Validation failed or invalid time range",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Room does not exist",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Room is already booked for this time",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BookingResponseDTO> create(@Valid @RequestBody CreateBookingRequestDTO request) {
        Booking created = bookingService.create(mapper.toCommand(request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a booking")
    @ApiResponse(responseCode = "200", description = "Booking updated")
    @ApiResponse(responseCode = "400", description = "Validation failed or invalid time range",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Booking or room does not exist",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Room is already booked for this time",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public BookingResponseDTO update(@Parameter(description = "Booking id", example = "1") @PathVariable Long id,
                                     @Valid @RequestBody UpdateBookingRequestDTO request) {
        return mapper.toResponse(bookingService.update(id, mapper.toCommand(request)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking", description = "Sets the booking status to CANCELLED.")
    @ApiResponse(responseCode = "200", description = "Booking cancelled")
    @ApiResponse(responseCode = "404", description = "Booking does not exist",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Booking cannot be cancelled in its current state",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public BookingResponseDTO cancel(@Parameter(description = "Booking id", example = "1") @PathVariable Long id) {
        return mapper.toResponse(bookingService.cancel(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a booking")
    @ApiResponse(responseCode = "204", description = "Booking deleted")
    @ApiResponse(responseCode = "404", description = "Booking does not exist",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> delete(@Parameter(description = "Booking id", example = "1") @PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
