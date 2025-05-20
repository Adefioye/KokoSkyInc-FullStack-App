package com.KokoSky.WeatherService.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime,
        List<String> errors
) {
    public ApiError(String path, String message, int statusCode, LocalDateTime localDateTime) {
        this(path, message, statusCode, localDateTime, List.of());
    }
}
