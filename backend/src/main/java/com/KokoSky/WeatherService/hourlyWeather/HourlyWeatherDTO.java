package com.KokoSky.WeatherService.hourlyWeather;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"hour_of_day", "temperature", "precipitation", "status"})
public class HourlyWeatherDTO {

    @JsonProperty("hour_of_day")
    @Range(min = 0, max = 23, message = "Hour of day must be in between 0-23")
    private int hourOfDay;

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;

    @NotBlank(message = "Status must not be empty")
    private String status;

    @Override
    public String toString() {
        return "HourlyWeatherDTO [hourOfDay=" + hourOfDay + ", temperature=" + temperature + ", precipitation="
                + precipitation + ", status=" + status + "]";
    }
}

