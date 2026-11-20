package com.example.tsis.holiday;

import com.example.tsis.config.HolidayClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class HolidayService {

    private static final Logger log = LoggerFactory.getLogger(HolidayService.class);

    private final HolidayApi api;
    private final HolidayClientProperties props;

    public HolidayService(HolidayApi api, HolidayClientProperties props) {
        this.api = api;
        this.props = props;
    }

    public HolidayResult getHolidays(int year) {
        String country = props.countryCode();
        try {
            List<HolidayDto> holidays = api.getPublicHolidays(year, country);
            if (holidays == null) {
                return fallback(year, country);
            }
            return new HolidayResult(year, country, HolidayResult.Source.REMOTE, holidays);
        } catch (RestClientException ex) {
            log.warn("Holiday service call failed, using fallback: {}", ex.getMessage());
            return fallback(year, country);
        }
    }

    private HolidayResult fallback(int year, String country) {
        return new HolidayResult(year, country, HolidayResult.Source.FALLBACK, List.of());
    }
}
