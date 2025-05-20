package com.KokoSky.WeatherService.dailyWeather;

import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DailyWeatherRepositoryTest {

    @Autowired
    private DailyWeatherRepository dailyWeatherRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testAdd() {
        String locationCode = "DANA_VN";
        String status = "Cloudy";
        int dayOfMonth = 16;
        int month = 7;
        int minTemp = 23;
        int maxTemp = 32;
        int precipitation = 40;

        // Create a Location instance
        Location location = Location.builder()
                    .code(locationCode)
                    .build();

        // Create DailyWeatherId
        DailyWeatherId id = DailyWeatherId.builder()
                .dayOfMonth(dayOfMonth)
                .month(month)
                .location(location)
                .build();

        // Create DailyWeather with builder
        DailyWeather dailyWeatherForecast = DailyWeather.builder()
                .id(id)
                .minTemp(minTemp)
                .maxTemp(maxTemp)
                .precipitation(precipitation)
                .status(status)
                .build();

        DailyWeather savedDailyWeather = dailyWeatherRepository.save(dailyWeatherForecast);

        assertThat(savedDailyWeather.getStatus()).isEqualTo(status);
    }

    @Test
    public void testDelete() {
        String locationCode = "DANA_VN";
        String status = "Cloudy";
        int dayOfMonth = 16;
        int month = 7;
        int minTemp = 23;
        int maxTemp = 32;
        int precipitation = 40;

        // Create a Location instance
        Location location = Location.builder()
                .code(locationCode)
                .build();

        // Create DailyWeatherId
        DailyWeatherId id = DailyWeatherId.builder()
                .dayOfMonth(dayOfMonth)
                .month(month)
                .location(location)
                .build();

        // Create DailyWeather with builder
        DailyWeather dailyWeatherForecast = DailyWeather.builder()
                .id(id)
                .minTemp(minTemp)
                .maxTemp(maxTemp)
                .precipitation(precipitation)
                .status(status)
                .build();

        dailyWeatherRepository.save(dailyWeatherForecast);

        // Delete dailyWeather data using its ID
        dailyWeatherRepository.deleteById(id);

        Optional<DailyWeather> result = dailyWeatherRepository.findById(id);

        assertThat(result).isNotPresent();
    }

    @Test
    public void testFindByLocationCodeFound() {
        // Arrange: Create and save Location
        String locationCode = "DELHI_IN";
        Location location = Location.builder()
                .code(locationCode)
                .cityName("Delhi")
                .regionName("Delhi")
                .countryCode("IN")
                .countryName("India")
                .build();

        locationRepository.save(location); // Assuming you have a locationRepo for Location entities

        // Create and save DailyWeather
        DailyWeatherId weatherId = new DailyWeatherId(16, 7, location);
        DailyWeather dailyWeather = DailyWeather.builder()
                .id(weatherId)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(10)
                .status("Clear")
                .build();

        dailyWeatherRepository.save(dailyWeather);  // Save to DailyWeather repository

        // Act
        List<DailyWeather> result = dailyWeatherRepository.findByLocationCode(locationCode);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getId().getLocation().getCode()).isEqualTo(locationCode);
    }

    @Test
    public void testFindByLocationCodeNotFound() {
        String locationCode = "ABC_XYZ";

        List<DailyWeather> result = dailyWeatherRepository.findByLocationCode(locationCode);

        assertThat(result).isEmpty();
    }

}
