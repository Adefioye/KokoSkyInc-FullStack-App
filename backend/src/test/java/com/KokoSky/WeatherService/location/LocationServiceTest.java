package com.KokoSky.WeatherService.location;

import com.KokoSky.WeatherService.exceptions.DuplicateResourceException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @InjectMocks
    private LocationService underTest;

    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
    }

    @Test
    public void whenAddLocation_returnNotNull() {

        // Given
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

        when(locationRepository.existsLocationByCode(code)).thenReturn(false);
        when(locationRepository.save(location)).thenReturn(location);

        // When
        Location newLocation = underTest.addLocation(location);

        // Then
        assertThat(newLocation).isNotNull();
    }

    @Test
    public void whenAddingLocation_andLocationExists_throwDuplicateResourceException() {

        // Given
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

        when(locationRepository.existsLocationByCode(code)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.addLocation(location))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Sorry! location code %s already exist!".formatted(code));

        // Then
        verify(locationRepository, times(1)).existsLocationByCode(code);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    public void whenGetLocations_returnLocations() {
        // Given
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

        when(locationRepository.findAllUntrashedLocations()).thenReturn(List.of(location));

        // When
        List<Location> locations = underTest.getLocations();

        // Then
        assertThat(locations.size()).isGreaterThan(0);
        verify(locationRepository, times(1)).findAllUntrashedLocations();
    }

    @Test
    public void whenGetLocationByCode_andLocationCodePresent_returnLocation() {
        // Given
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

        when(locationRepository.findByCode(code)).thenReturn((location));

        // When
        Location locationByCode = underTest.getLocationByCode(code);

        // Then
        assertThat(locationByCode).isNotNull();
        verify(locationRepository, times(1)).findByCode(code);
    }

    @Test
    public void whenGetLocationByCode_andLocationCodeAbsent_throwResourceNotFoundException() {
        // Given
        String code = "LACA_US";

        when(locationRepository.findByCode(code)).thenReturn(null);

        // When and Then
        assertThatThrownBy(() -> underTest.getLocationByCode(code))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessage("No location found with the given code: %s".formatted(code));
    }

    @Test
    public void whenUpdatingLocation_andLocationCodeAbsent_throwResourceNotFoundException() {
        // Given
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;

        Location newLocation = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();

        // When
        when(locationRepository.existsLocationByCode(code)).thenReturn(false);

         // Then
        assertThatThrownBy(() -> underTest.updateLocationByCode(newLocation))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessage("No location found with the given code: %s".formatted(code));
    }

    @Test
    public void whenUpdatingLocation_andLocationCodePresent_returnUpdatedLocation() {
        // Given
        String code = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        String newCityName = "Los Angeles City";
        String newRegionName = "California State";

        Location location = Location
                .builder()
                .code(code)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();
        Location newLocation = Location
                .builder()
                .code(code)
                .cityName(newCityName)
                .regionName(newRegionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .build();

        when(locationRepository.existsLocationByCode(code)).thenReturn(true);
        when(locationRepository.save(any(Location.class))).thenReturn(newLocation);
        // When
        Location updatedLocation = underTest.updateLocationByCode(newLocation);
        // Then
        assertThat(updatedLocation).isNotNull();
        assertThat(updatedLocation.getCityName()).isEqualTo(newCityName);
        assertThat(updatedLocation.getRegionName()).isEqualTo(newRegionName);
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    public void whenDeletingLocationByCode_andLocationCodeAbsent_throwResourceNotFoundException() {
        // Given
        String code = "LACA_US";
        Location newLocation = new Location();

        // When
        when(locationRepository.existsLocationByCode(code)).thenReturn(false);

        // Then
        assertThatThrownBy(() -> underTest.deleteLocationByCode(code))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessage("No location found with the given code: %s".formatted(code));

        verify(locationRepository, never()).deleteById(code);
    }

    @Test
    public void whenDeletingLocationByCode_andLocationCodePresent_deleteLocation() {
        // Given
        String code = "LACA_US";

        // When
        when(locationRepository.existsLocationByCode(code)).thenReturn(true);

        // Then
       underTest.deleteLocationByCode(code);
       verify(locationRepository, times(1)).softDeleteByCode(code);
    }

}