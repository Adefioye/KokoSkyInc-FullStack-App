package com.KokoSky.WeatherService.dailyWeather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeatherListDTO {

    private String location;

    @JsonProperty("daily_forecast")
    @Builder.Default
    private List<DailyWeatherDTO> dailyForecast = new ArrayList<>();

    public void addDailyWeatherDTO(DailyWeatherDTO dto) {
        this.dailyForecast.add(dto);
    }
}
