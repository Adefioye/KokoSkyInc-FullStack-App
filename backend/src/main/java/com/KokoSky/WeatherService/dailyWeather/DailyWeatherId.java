package com.KokoSky.WeatherService.dailyWeather;

import com.KokoSky.WeatherService.location.Location;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeatherId implements Serializable {

    private int dayOfMonth;
    private int month;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;

    @Override
    public String toString() {
        return "DailyWeatherId [dayOfMonth=" + dayOfMonth + ", month=" + month + ", location=" + location + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfMonth, location, month);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DailyWeatherId other = (DailyWeatherId) obj;
        return dayOfMonth == other.dayOfMonth && Objects.equals(location, other.location) && month == other.month;
    }
}
