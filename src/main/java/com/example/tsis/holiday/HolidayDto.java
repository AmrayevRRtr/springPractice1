package com.example.tsis.holiday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "A public holiday")
public record HolidayDto(
        @Schema(example = "2026-12-16") LocalDate date,
        @Schema(example = "Тәуелсіздік күні") String localName,
        @Schema(example = "Independence Day") String name
) {
}
