package com.KokoSky.WeatherService.fullWeather;

import com.KokoSky.WeatherService.dailyWeather.DailyWeatherDTO;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeatherDTO;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeatherDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
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
public class FullWeatherDTO {

    private String location;

    @JsonProperty("realtime_weather")
    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RealtimeWeatherFieldFilter.class)
    @Builder.Default
    @Valid
    private RealtimeWeatherDTO realtimeWeather = new RealtimeWeatherDTO();

    @JsonProperty("hourly_forecast")
    @Builder.Default
    @Valid
    private List<HourlyWeatherDTO> listHourlyWeather = new ArrayList<>();

    @JsonProperty("daily_forecast")
    @Builder.Default
    @Valid
    private List<DailyWeatherDTO> listDailyWeather = new ArrayList<>();
}
