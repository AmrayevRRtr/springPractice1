package com.example.tsis.holiday;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;

@RestController
@RequestMapping("/api/v1/holidays")
@Tag(name = "Holidays", description = "Public holidays from an external service, with a fallback")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping
    @Operation(summary = "Get public holidays",
            description = "Calls the external holidays service with configured timeouts. "
                    + "If it is down or too slow, 200 is still returned with source=FALLBACK and an empty list.")
    @ApiResponse(responseCode = "200", description = "Holidays (fresh or fallback)",
            content = @Content(schema = @Schema(implementation = HolidayResult.class)))
    @ApiResponse(responseCode = "400", description = "Invalid year",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)))
    public HolidayResult getHolidays(
            @Parameter(description = "Calendar year, defaults to the current year", example = "2026")
            @RequestParam(required = false) Integer year) {
        return holidayService.getHolidays(year != null ? year : Year.now().getValue());
    }
}
