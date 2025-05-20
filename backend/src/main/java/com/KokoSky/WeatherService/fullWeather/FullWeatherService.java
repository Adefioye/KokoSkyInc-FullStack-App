package com.KokoSky.WeatherService.fullWeather;

import com.KokoSky.WeatherService.dailyWeather.DailyWeather;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeather;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.location.LocationRepository;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeather;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FullWeatherService {

    private LocationRepository locationRepository;

    public FullWeatherService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location getByLocation(Location locationFromIP) {
        String cityName = locationFromIP.getCityName();
        String countryCode = locationFromIP.getCountryCode();

        Location locationInDB = locationRepository.findByCountryCodeAndCityName(countryCode, cityName);

        if (locationInDB == null) {
            throw new LocationNotFoundException(countryCode, cityName);
        }

        return locationInDB;
    }

    public Location get(String locationCode) {
        Location location = locationRepository.findByCode(locationCode);

        if (location == null) {
            throw new LocationNotFoundException(locationCode);
        }

        return location;
    }

    public Location update(String locationCode, Location locationInRequest) {
        Location locationInDB = locationRepository.findByCode(locationCode);

        if (locationInDB == null) {
            throw new LocationNotFoundException(locationCode);
        }

        // Update associations to point to locationInDB
        RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
        realtimeWeather.setLocation(locationInDB);
        realtimeWeather.setLastUpdated(new Date());

        if (locationInDB.getRealtimeWeather() == null) {
            locationInDB.setRealtimeWeather(realtimeWeather);
            locationRepository.save(locationInDB);
        }

        List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
        listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));

        List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
        listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDB));

        // Build a new Location using builder
        Location updatedLocation = Location.builder()
                .code(locationInDB.getCode())
                .cityName(locationInDB.getCityName())
                .regionName(locationInDB.getRegionName())
                .countryCode(locationInDB.getCountryCode())
                .countryName(locationInDB.getCountryName())
                .enabled(locationInDB.isEnabled())
                .trashed(locationInDB.isTrashed())
                .build();

        return locationRepository.save(updatedLocation);
    }

}
