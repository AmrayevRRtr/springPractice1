package com.example.tsis.holiday;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(accept = "application/json")
public interface HolidayApi {

    @GetExchange("/PublicHolidays/{year}/{countryCode}")
    List<HolidayDto> getPublicHolidays(@PathVariable int year, @PathVariable String countryCode);
}
