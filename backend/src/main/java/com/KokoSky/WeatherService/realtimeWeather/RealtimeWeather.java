package com.KokoSky.WeatherService.realtimeWeather;

import com.KokoSky.WeatherService.location.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {

    @Id
    @Column(name = "location_code")
    @JsonIgnore
    private String locationCode;

    private int temperature;

    private int humidity;

    private int precipitation;

    private int windSpeed;

    @Column(length = 50)
    @NotBlank(message = "Status must not be empty")
    @Length(min = 3, max = 50, message = "Status must be in between 3-50 characters")
    private String status;

    @JsonProperty("last_updated")
    private Date lastUpdated;

    @OneToOne
    @JoinColumn(name = "location_code")
    @MapsId
    private Location location;

    public void setLocation(Location location) {
        this.location = location;
        this.locationCode = location.getCode(); // Ensures consistency
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationCode); // Primary key is stable and unique
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof RealtimeWeather other)) return false;
        return Objects.equals(locationCode, other.locationCode);
    }
}
