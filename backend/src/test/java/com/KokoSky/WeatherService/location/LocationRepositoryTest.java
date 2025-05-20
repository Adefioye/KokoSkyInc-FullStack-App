package com.KokoSky.WeatherService.location;

import com.KokoSky.WeatherService.dailyWeather.DailyWeather;
import com.KokoSky.WeatherService.dailyWeather.DailyWeatherId;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeather;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeatherId;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    public void whenLocationCodeExists_returnTrue() {
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();

        underTest.save(location);
        boolean isLocationPresent = underTest.existsLocationByCode(location.getCode());

        assertThat(isLocationPresent).isTrue();
    }

    @Test
    public void whenLocationCodeDoesNotExist_returnFalse() {
        String nonExistentLocationCode = "BWR_NG";
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();

        underTest.save(location);
        boolean isLocationPresent = underTest.existsLocationByCode(nonExistentLocationCode);

        assertThat(isLocationPresent).isFalse();
    }

    @Test
    public void whenGivenLocation_andTrashedIsTrue_returnEmptyList() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = true;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);
        List<Location> allUntrashedLocations = underTest.findAllUntrashedLocations();

        // Then
        assertThat(allUntrashedLocations.size()).isEqualTo(0);
    }

    @Test
    public void whenGivenLocation_andTrashedIsFalse_returnLocation() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);
        List<Location> allUntrashedLocations = underTest.findAllUntrashedLocations();

        // Then
        assertThat(allUntrashedLocations.size()).isEqualTo(1);
    }

    @Test
    public void whenGivenLocationCode_andPresent_returnLocation() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);
        Optional<Location> locationByCode = underTest.findUntrashedLocationsByCode(code);

        // Then
        assertThat(locationByCode).isPresent();
    }

    @Test
    public void whenGivenLocationCode_andAbsent_returnNull() {
        // When
        String nonExistentLocationCode = "LACA_US";

        // When
        Optional<Location> locationByCode = underTest.findUntrashedLocationsByCode(nonExistentLocationCode);

        // Then
        assertThat(locationByCode).isNotPresent();
    }
    
    @Test
    public void testRealTimeWeather_addedSuccesfullyThroughLocation() {
        // Given
        String locationCode = "LACA_US";
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

        RealtimeWeather realTimeWeather = RealtimeWeather
                .builder()
                .locationCode(locationCode)
                .temperature(15)
                .humidity(32)
                .precipitation(57)
                .windSpeed(40)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set both location and realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        // When
        underTest.save(location);
        Optional<Location> savedLocation = underTest.findUntrashedLocationsByCode(locationCode);

        // Then
        assertThat(savedLocation).isPresent();
        assertThat(savedLocation.get().getRealtimeWeather().getLocationCode()).isEqualTo(locationCode);
    }

    @Test
    public void testAddHourlyWeatherData() {
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();

        underTest.save(location);

        Location expectedLocation = underTest.findByCode(code);
        List<HourlyWeather> listOfHourWeather = expectedLocation.getListHourlyWeather();
        assertThat(listOfHourWeather).isEmpty();

        HourlyWeatherId hourlyWeatherId1 = HourlyWeatherId
                                                .builder()
                                                .location(location)
                                                .hourOfDay(10)
                                                .build();

        HourlyWeatherId hourlyWeatherId2 = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(11)
                .build();

        HourlyWeather hourlyForecast1 = HourlyWeather.builder()
                .id(hourlyWeatherId1)
                .temperature(15)
                .precipitation(40)
                .status("Sunny")
                .build();

        HourlyWeather hourlyForecast2 = HourlyWeather.builder()
                .id(hourlyWeatherId2)
                .temperature(15)
                .precipitation(40)
                .status("Sunny")
                .build();

        // Add hourly weather forecasts and verify that they are correctly added
        listOfHourWeather.add(hourlyForecast1);
        listOfHourWeather.add(hourlyForecast2);

        assertThat(listOfHourWeather).isNotEmpty();
        assertThat(listOfHourWeather.size()).isEqualTo(2);
    }

    @Test
    public void testFindByCountryCodeAndCityNameIsSuccessful() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);
        Location actual = underTest.findByCountryCodeAndCityName(countryCode, cityName);

        // Then
        assertThat(actual).isNotNull();
    }

    @Test
    public void testFindByCountryCodeAndCityNameNotSuccessful() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);
        Location actual = underTest.findByCountryCodeAndCityName(countryCode, cityName);

        // If trashed is false, it is not NULL
        assertThat(actual).isNotNull();

        // If trashed is true, it is NULL
        location.setTrashed(true);
        underTest.save(location);

        Location secondActual = underTest.findByCountryCodeAndCityName(countryCode, cityName);
        assertThat(secondActual).isNull();

        // If countryCode and cityName are absent in database, return NULL
        String missingCountryCode = "IN";
        String missingCityName = "DELHI";

        Location thirdActual = underTest.findByCountryCodeAndCityName(missingCountryCode, missingCityName);
        assertThat(thirdActual).isNull();
    }

    @Test
    public void testAddDailyWeatherData() {
        // When
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        // When
        underTest.save(location);

        // Fetch existing location
        Optional<Location> fetchedLocation = underTest.findById(code);

        assertThat(fetchedLocation).isPresent();

        // Create DailyWeather instances with embedded ID
        DailyWeatherId id1 = new DailyWeatherId(16, 7, location);
        DailyWeather forecast1 = DailyWeather.builder()
                .id(id1)
                .minTemp(25)
                .maxTemp(33)
                .precipitation(20)
                .status("Sunny")
                .build();

        DailyWeatherId id2 = new DailyWeatherId(17, 7, location);
        DailyWeather forecast2 = DailyWeather.builder()
                .id(id2)
                .minTemp(26)
                .maxTemp(34)
                .precipitation(10)
                .status("Clear")
                .build();

        // Add to location's list
        location.getListDailyWeather().add(forecast1);
        location.getListDailyWeather().add(forecast2);

        // Save the parent entity (due to CascadeType.ALL, children will be saved too)
        Location updatedLocation = underTest.save(location);

        // Assert
        assertThat(updatedLocation.getListDailyWeather()).isNotEmpty();
        assertThat(updatedLocation.getListDailyWeather()).hasSizeGreaterThanOrEqualTo(2);
    }

}
