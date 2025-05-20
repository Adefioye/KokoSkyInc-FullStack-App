package com.KokoSky.WeatherService.dailyWeather;

import com.KokoSky.WeatherService.exceptions.BadRequestException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.utility.CommonUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/daily")
public class DailyWeatherController {

    private final DailyWeatherService dailyWeatherService;
    private final GeolocationService geolocationService;
    private final ModelMapper modelMapper;

    public DailyWeatherController(
            DailyWeatherService dailyWeatherService,
            GeolocationService locationService,
            ModelMapper modelMapper) {
        this.dailyWeatherService = dailyWeatherService;
        this.geolocationService = locationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) {
        String ipAddress = CommonUtility.getIPAddress(request);

        Location locationFromIP = geolocationService.getLocation(ipAddress);
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listEntity2DTO(dailyForecast));
    }

    @GetMapping("{locationCode}")
    public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode){
        List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);

        if (dailyForecast.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listEntity2DTO(dailyForecast));
    }

    @PutMapping("/{locationCode}")
    public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code,
                                                 @RequestBody @Valid List<DailyWeatherDTO> listDTO) throws BadRequestException {

        if (listDTO.isEmpty()) {
            throw new BadRequestException("Daily forecast data cannot be empty");
        }

        listDTO.forEach(System.out::println);

        List<DailyWeather> dailyWeather = listDTO2ListEntity(listDTO);

        System.out.println("================");

        dailyWeather.forEach(System.out::println);

        List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeather);

        return ResponseEntity.ok(listEntity2DTO(updatedForecast));
    }

    private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
        Location location = dailyForecast.get(0).getId().getLocation();

        DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
        listDTO.setLocation(location.toString());

        dailyForecast.forEach(dailyWeather -> {
            listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
        });

        return listDTO;
    }

    private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> listDTO) {
        List<DailyWeather> listEntity = new ArrayList<>();

        listDTO.forEach(dto -> {
            listEntity.add(modelMapper.map(dto, DailyWeather.class));
        });

        return listEntity;
    }

}
