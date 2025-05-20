package com.KokoSky.WeatherService.realtimeWeather;


import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RealtimeWeatherRepositoryTest {

    @Autowired
    private RealtimeWeatherRepository underTest;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    public void testRealtimeWeather_isSavedSuccessfully() {
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
                .temperature(75)
                .humidity(50)
                .precipitation(1015)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        // Save location
        locationRepository.save(location);

        RealtimeWeather updatedRealtimeWeather = location.getRealtimeWeather();

        assertThat(updatedRealtimeWeather.getLocationCode()).isEqualTo(locationCode);
    }

    @Test
    public void testFindByLocationCodeAndCityNotFound() {
        String locationCode = "LACA_US";
        String cityName = "Los Angeles";

        RealtimeWeather realTimeWeather = underTest.findByCountryCodeAndCity(locationCode, cityName);

        assertThat(realTimeWeather).isNull();
    }

    @Test
    public void testFindByLocationCodeAndCityFound() {
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
                .temperature(15)
                .humidity(30)
                .precipitation(15)
                .windSpeed(43)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        // Save location
        locationRepository.save(location);

        // Find by location code and City and validate that it is present
        RealtimeWeather actual = underTest.findByCountryCodeAndCity(countryCode, cityName);

        assertThat(actual).isNotNull();
        assertThat(actual.getLocation().getCityName()).isEqualTo(cityName);
        assertThat(actual.getHumidity()).isEqualTo(30);

    }

    @Test
    public void testFindByLocationCodeReturnNull_ifLocationCodeNotFound() {
        String locationCode = "ABCXYZ";
        RealtimeWeather realtimeWeather = underTest.findByLocationCode(locationCode);

        assertThat(realtimeWeather).isNull();
    }

    @Test
    public void testFindByLocationCodeReturnNull_ifLocationIsTrashed() {
        // Given
        String locationCode = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = true;


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
                .temperature(5)
                .humidity(15)
                .precipitation(40)
                .windSpeed(35)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        // Save location
        locationRepository.save(location);

        RealtimeWeather actual = underTest.findByLocationCode(locationCode);

        assertThat(actual).isNull();
    }

    @Test
    public void testFindByLocationCodeReturnNotNull_ifLocationIsNotTrashed() {
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
                .temperature(15)
                .humidity(20)
                .precipitation(35)
                .windSpeed(40)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        // Save location
        locationRepository.save(location);

        RealtimeWeather actual = underTest.findByLocationCode(locationCode);

        assertThat(actual).isNotNull();
        assertThat(actual.getLocationCode()).isEqualTo(locationCode);
        assertThat(actual.getHumidity()).isEqualTo(20);
    }
}
