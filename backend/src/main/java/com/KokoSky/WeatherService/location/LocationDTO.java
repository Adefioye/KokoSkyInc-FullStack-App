package com.KokoSky.WeatherService.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@JsonPropertyOrder({"code", "city_name", "region_name", "country_code", "country_name", "enabled"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {

    @NotNull(message = "Location code cannot be null")
    @Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
    private String code;

    @JsonProperty("city_name")
    @NotNull(message = "City name cannot be null")
    @Length(min = 3, max = 128, message = "City name must have 3-128 characters")
    private String cityName;

    @JsonProperty("region_name")
    @Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String regionName;

    @JsonProperty("country_name")
    @NotNull(message = "Country name cannot be null")
    @Length(min = 3, max = 64, message = "Country name must have 3-64 characters")
    private String countryName;

    @JsonProperty("country_code")
    @NotNull(message = "Country code cannot be null")
    @Length(min = 2, max = 2, message = "Country code must have 2 characters")
    private String countryCode;

    private boolean enabled;
}
