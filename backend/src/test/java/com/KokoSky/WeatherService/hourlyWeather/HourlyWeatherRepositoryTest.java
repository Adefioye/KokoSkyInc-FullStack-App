package com.KokoSky.WeatherService.hourlyWeather;

import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HourlyWeatherRepositoryTest {

    @Autowired
    private HourlyWeatherRepository underTest;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testAdd() {
        String locationCode = "DELHI_IN";
        int hourOfDay = 12;

        Location location = Location.builder().code(locationCode).build();
        HourlyWeatherId hourlyWeatherId = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(hourOfDay)
                .build();

        HourlyWeather forecast = HourlyWeather
                .builder()
                .id(hourlyWeatherId)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeather updatedForecast = underTest.save(forecast);

        assertThat(updatedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
        assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
    }

    @Test
    public void testDelete() {
        String locationCode = "LACA_US";
        int hourOfDay = 12;

        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;

        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();
        HourlyWeatherId hourlyWeatherId = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(hourOfDay)
                .build();

        HourlyWeather forecast = HourlyWeather
                .builder()
                .id(hourlyWeatherId)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        underTest.save(forecast);
        HourlyWeather updatedForecast = underTest.save(forecast);
        assertThat(updatedForecast).isNotNull();

        // Delete from repository
        underTest.deleteById(hourlyWeatherId);

        Optional<HourlyWeather> expectedResult = underTest.findById(hourlyWeatherId);

        assertThat(expectedResult).isNotPresent();
    }

    @Test
    public void testFindByLocationCodeAndHourIsSuccessful() {
        String locationCode = "LACA_US";
        int currentHour = 12;
        int hourOfDay = 14;

        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();
        HourlyWeatherId hourlyWeatherId = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(hourOfDay)
                .build();

        HourlyWeather forecast = HourlyWeather
                .builder()
                .id(hourlyWeatherId)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();


        location.getListHourlyWeather().add(forecast);
        locationRepository.save(location);

        // Test and validate FindByLocationCodeAndHour
        // This helps get upcoming hourly data, that is, hours greater than currentHour
        List<HourlyWeather> actual = underTest.findByLocationCodeAndHour(locationCode, currentHour);

        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    public void testFindByLocationCodeAndHourIsNotSuccessful() {
        String locationCode = "LACA_US";
        int hourOfDay = 12;
        int currentHour = 14;

        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();
        HourlyWeatherId hourlyWeatherId = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(hourOfDay)
                .build();

        HourlyWeather forecast = HourlyWeather
                .builder()
                .id(hourlyWeatherId)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();


        location.getListHourlyWeather().add(forecast);
        locationRepository.save(location);

        // Test and validate FindByLocationCodeAndHour is empty for missing location code
        String missingLocationCode = "DELHI_IN";
        List<HourlyWeather> firstActual = underTest.findByLocationCodeAndHour(missingLocationCode, hourOfDay);

        assertThat(firstActual).isEmpty();

        // Test and validate FindByLocationCodeAndHour is empty for if currentHour > than all hours of saved entries in HourlyWeather
        List<HourlyWeather> secondActual = underTest.findByLocationCodeAndHour(locationCode, currentHour);

        assertThat(secondActual).isEmpty();
    }

}
