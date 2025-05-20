package com.KokoSky.WeatherService.hourlyWeather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HourlyWeatherListDTO {

    private String location;

    @JsonProperty("hourly_forecast")
    private List<HourlyWeatherDTO> hourlyForecast = new ArrayList<>();

    public void addWeatherHourlyDTO(HourlyWeatherDTO dto) {
        this.hourlyForecast.add(dto);
    }
}

