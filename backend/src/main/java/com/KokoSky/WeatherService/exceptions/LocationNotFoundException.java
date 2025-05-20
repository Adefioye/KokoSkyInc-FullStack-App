package com.KokoSky.WeatherService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String locationCode) {
        super("No location found with the given code: " + locationCode);
    }
    
    public LocationNotFoundException(String countryCode, String cityName) {
        super("No location found with the given country code: " + countryCode + " and city name: " + cityName);
    }
}
