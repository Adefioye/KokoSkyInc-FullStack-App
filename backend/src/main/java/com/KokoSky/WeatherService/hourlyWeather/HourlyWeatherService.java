package com.KokoSky.WeatherService.hourlyWeather;

import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HourlyWeatherService {
    private final HourlyWeatherRepository hourlyWeatherRepository;
    private final LocationRepository locationRepository;

    public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepository, LocationRepository locationRepository) {
        super();
        this.hourlyWeatherRepository = hourlyWeatherRepository;
        this.locationRepository = locationRepository;
    }

    public List<HourlyWeather> getByLocation(Location location, int currentHour) throws LocationNotFoundException {
        String countryCode = location.getCountryCode();
        String cityName = location.getCityName();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return hourlyWeatherRepository.findByLocationCodeAndHour(locationInDB.getCode(), currentHour);
    }

    public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) throws LocationNotFoundException {

        Location locationInDB = locationRepository.findByCode(locationCode);

        if (locationInDB == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return hourlyWeatherRepository.findByLocationCodeAndHour(locationCode, currentHour);
    }

    public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyWeatherInRequest) {
        Location location = locationRepository.findByCode(locationCode);

        for (HourlyWeather item : hourlyWeatherInRequest) {
            item.getId().setLocation(location);
        }

        List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
        List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();

        // Here we remove all item in DB currently not in hourlyWeatherRequest
        for (HourlyWeather item : hourlyWeatherInDB) {
            if (!hourlyWeatherInRequest.contains(item)) {
                hourlyWeatherToBeRemoved.add(item.getShallowCopy());
            }
        }

        for (HourlyWeather item : hourlyWeatherToBeRemoved) {
            hourlyWeatherInDB.remove(item);
        }

        return (List<HourlyWeather>) hourlyWeatherRepository.saveAll(hourlyWeatherInRequest);
    }
}
