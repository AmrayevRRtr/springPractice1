package com.example.tsis.holiday;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Public holidays for a year and where the data came from")
public record HolidayResult(
        int year,
        String countryCode,
        @Schema(description = "REMOTE = fresh data from the external service, "
                + "FALLBACK = the service was unavailable and an empty list is returned")
        Source source,
        List<HolidayDto> holidays
) {
    public enum Source { REMOTE, FALLBACK }
}
