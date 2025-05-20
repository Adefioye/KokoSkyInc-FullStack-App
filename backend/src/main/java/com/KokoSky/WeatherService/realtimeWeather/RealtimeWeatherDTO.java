package com.KokoSky.WeatherService.realtimeWeather;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"location", "temperature", "humidity", "precipitation", "wind_speed", "status", "last_updated"})
public class RealtimeWeatherDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String location;

    @Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
    private int temperature;

    @Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 percentage")
    private int humidity;

    @Range(min = 0, max = 100, message = "Precipitation must be in the range of 0 to 100 percentage")
    private int precipitation;

    @JsonProperty("wind_speed")
    @Range(min = 0, max = 200, message = "Wind speed must be in the range of 0 to 200 km/h")
    private int windSpeed;

    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;

    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private Date lastUpdated;
}
