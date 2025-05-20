package com.KokoSky.WeatherService.realtimeWeather;

import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/realtime")
public class RealtimeWeatherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherController.class);
    private final GeolocationService geolocationService;
    private final RealtimeWeatherService realtimeWeatherService;
    private final ModelMapper modelMapper;

    public RealtimeWeatherController(
            GeolocationService geolocationService,
            RealtimeWeatherService realtimeWeatherService,
            ModelMapper modelMapper
    ) {
        this.geolocationService = geolocationService;
        this.realtimeWeatherService = realtimeWeatherService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationByIPAddress = geolocationService.getLocation(ipAddress);
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationByIPAddress);

        return ResponseEntity.ok(entity2DTO(realtimeWeather));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
        RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
        RealtimeWeatherDTO dto = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateRealtimeWeather(@PathVariable("locationCode") String locationCode,
                                                   @RequestBody @Valid RealtimeWeatherDTO dto) {

        RealtimeWeather realtimeWeather = dto2Entity(dto);
        realtimeWeather.setLocationCode(locationCode);

        RealtimeWeather updatedRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeather);

        return ResponseEntity.ok(entity2DTO(updatedRealtimeWeather));
    }

    private RealtimeWeatherDTO entity2DTO(RealtimeWeather realtimeWeather) {
        return modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
    }

    private RealtimeWeather dto2Entity(RealtimeWeatherDTO dto) {
        return modelMapper.map(dto, RealtimeWeather.class);
    }
}
