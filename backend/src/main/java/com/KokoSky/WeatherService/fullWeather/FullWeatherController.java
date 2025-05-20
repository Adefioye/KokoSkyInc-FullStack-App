package com.KokoSky.WeatherService.fullWeather;

import com.KokoSky.WeatherService.exceptions.BadRequestException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/full")
public class FullWeatherController {

    private final GeolocationService geolocationService;
    private final FullWeatherService fullWeatherService;
    private final ModelMapper modelMapper;

    public FullWeatherController(
            GeolocationService locationService,
            FullWeatherService weatherService,
            ModelMapper modelMapper) {
        this.geolocationService = locationService;
        this.fullWeatherService = weatherService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = geolocationService.getLocation(ipAddress);
        Location locationInDB = fullWeatherService.getByLocation(locationFromIP);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable String locationCode) {

        Location locationInDB = fullWeatherService.get(locationCode);

        return ResponseEntity.ok(entity2DTO(locationInDB));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateFullWeather(@PathVariable String locationCode,
                                               @RequestBody @Valid FullWeatherDTO dto) throws BadRequestException {

        if (dto.getListHourlyWeather().isEmpty()) {
            throw new BadRequestException("Hourly weather data cannot be empty");
        }

        if (dto.getListDailyWeather().isEmpty()) {
            throw new BadRequestException("Daily weather data cannot be empty");
        }

        Location locationInRequest = dto2Entity(dto);

        Location updatedLocation = fullWeatherService.update(locationCode, locationInRequest);

        return ResponseEntity.ok(entity2DTO(updatedLocation));
    }

    private FullWeatherDTO entity2DTO(Location entity) {
        FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);

        // Do not show the field location in realtime_weather object
        dto.getRealtimeWeather().setLocation(null);
        return dto;
    }

    private Location dto2Entity(FullWeatherDTO dto) {
        return modelMapper.map(dto, Location.class);
    }
}
