package com.example.tsis.booking.web;

import com.example.tsis.booking.app.BookingCommand;
import com.example.tsis.booking.app.BookingUseCase;
import com.example.tsis.booking.domain.*;
import com.example.tsis.common.NotFoundException;
import com.example.tsis.common.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import({BookingWebMapper.class, GlobalExceptionHandler.class})
class BookingControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BookingUseCase service;

    static final String VALID_JSON = """
            {"roomId":1,"guestName":"John Doe",
             "startTime":"2030-05-01T10:00:00","endTime":"2030-05-01T12:00:00"}""";

    Booking sample() {
        return Booking.restore(7L, 1L, "John Doe",
                LocalDateTime.of(2030, 5, 1, 10, 0), LocalDateTime.of(2030, 5, 1, 12, 0),
                BookingStatus.CONFIRMED);
    }

    @Test
    void create_returns201_withLocationAndBody() throws Exception {
        when(service.create(any(BookingCommand.class))).thenReturn(sample());

        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/bookings/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    void create_returns400_whenGuestNameBlank() throws Exception {
        String bad = VALID_JSON.replace("John Doe", "");

        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(bad))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"));
    }

    @Test
    void create_returns409_onConflict() throws Exception {
        when(service.create(any(BookingCommand.class))).thenThrow(new BookingConflictException("busy"));

        mvc.perform(post("/api/v1/bookings").contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getById_returns404_whenMissing() throws Exception {
        when(service.getById(99L)).thenThrow(new NotFoundException("Booking with id 99 not found"));

        mvc.perform(get("/api/v1/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void getAll_returnsPage() throws Exception {
        when(service.getAll(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sample())));

        mvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].guestName").value("John Doe"));
    }
}