package com.KokoSky.WeatherService.realtimeWeather;

import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RealtimeWeatherService {

    private final RealtimeWeatherRepository realtimeWeatherRepository;
    private final LocationRepository locationRepository;

    public RealtimeWeatherService(
            RealtimeWeatherRepository realtimeWeatherRepository,
            LocationRepository locationRepository
    ) {
        this.realtimeWeatherRepository = realtimeWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public RealtimeWeather getByLocation(Location location) {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByCountryCodeAndCity(countryCode, cityName);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return realtimeWeather;
    }

    public RealtimeWeather getByLocationCode(String locationCode) {

        RealtimeWeather realtimeWeather = realtimeWeatherRepository.findByLocationCode(locationCode);

        if (realtimeWeather == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return realtimeWeather;
    }

    public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather) {
        Location location = locationRepository.findByCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException(locationCode);
        }

        realtimeWeather.setLocation(location);
        realtimeWeather.setLastUpdated(new Date());

        if (location.getRealtimeWeather() == null) {
            location.setRealtimeWeather(realtimeWeather);
            Location updatedLocation = locationRepository.save(location);

            return updatedLocation.getRealtimeWeather();
        }

        return realtimeWeatherRepository.save(realtimeWeather);
    }
}
