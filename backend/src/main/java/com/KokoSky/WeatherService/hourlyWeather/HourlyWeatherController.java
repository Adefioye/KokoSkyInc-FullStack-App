package com.KokoSky.WeatherService.hourlyWeather;

import com.KokoSky.WeatherService.exceptions.BadRequestException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hourly")
@Validated
public class HourlyWeatherController {
    private final HourlyWeatherService hourlyWeatherService;
    private final GeolocationService locationService;
    private final ModelMapper modelMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(HourlyWeatherController.class);

    public HourlyWeatherController(
            HourlyWeatherService hourlyWeatherService,
            GeolocationService locationService,
            ModelMapper modelMapper
            ) {
        super();
        this.hourlyWeatherService = hourlyWeatherService;
        this.locationService = locationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
            Location locationFromIP = locationService.getLocation(ipAddress);

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIP, currentHour);

            if (hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
        } catch (NumberFormatException e) {
            LOGGER.error(String.format("%s from X-Current-Hour header", e.getMessage()));
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<?> listHourlyForecastByLocationCode(
            @PathVariable("locationCode") String locationCode, HttpServletRequest request) {

        try {
            int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));

            List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);

            if (hourlyForecast.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(listEntity2DTO(hourlyForecast));

        } catch (NumberFormatException ex) {

            return ResponseEntity.badRequest().build();

        } catch (LocationNotFoundException ex) {

            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode,
                                                  @RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException {

        if (listDTO.isEmpty()) {
            throw new BadRequestException("Hourly forecast data cannot be empty");
        }

        listDTO.forEach(System.out::println);

        List<HourlyWeather> listHourlyWeather = listDTO2ListEntity(listDTO);

        List<HourlyWeather> updateHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);

        return ResponseEntity.ok(listEntity2DTO(updateHourlyWeather));
    }

    private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecast) {
        Location location = hourlyForecast.get(0).getId().getLocation();

        HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
        listDTO.setLocation(location.toString());

        hourlyForecast.forEach(hourlyWeather -> {
            HourlyWeatherDTO dto = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
            listDTO.addWeatherHourlyDTO(dto);
        });

        return listDTO;
    }

    private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO) {
        List<HourlyWeather> listEntity = new ArrayList<>();

        listDTO.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, HourlyWeather.class));
        });

        return listEntity;
    }


}
