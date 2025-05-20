package com.KokoSky.WeatherService.hourlyWeather;

import com.KokoSky.WeatherService.location.Location;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Builder
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HourlyWeatherId implements Serializable {

    private int hourOfDay;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;
}
