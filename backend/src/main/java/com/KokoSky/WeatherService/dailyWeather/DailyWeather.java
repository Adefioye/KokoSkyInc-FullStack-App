package com.KokoSky.WeatherService.dailyWeather;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "weather_daily")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeather {

    @EmbeddedId
    private DailyWeatherId id = new DailyWeatherId();

    private int minTemp;
    private int maxTemp;
    private int precipitation;

    @Column(length = 50)
    private String status;

    public DailyWeather getShallowCopy() {
        DailyWeather copy = new DailyWeather();
        copy.setId(this.getId());

        return copy;
    }

    @Override
    public String toString() {
        return "DailyWeather [id=" + id + ", minTemp=" + minTemp + ", maxTemp=" + maxTemp + ", precipitation="
                + precipitation + ", status=" + status + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DailyWeather other = (DailyWeather) obj;
        return Objects.equals(id, other.id);
    }
}
