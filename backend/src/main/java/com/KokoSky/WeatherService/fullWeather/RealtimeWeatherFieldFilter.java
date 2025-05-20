package com.KokoSky.WeatherService.fullWeather;

import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeatherDTO;

public class RealtimeWeatherFieldFilter {

    public boolean equals(Object object) {

        if (object instanceof RealtimeWeatherDTO) {
            RealtimeWeatherDTO dto = (RealtimeWeatherDTO) object;
            return dto.getStatus() == null;
        }

        return false;
    }
}
