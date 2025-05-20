package com.KokoSky.WeatherService.fullWeather;

import com.KokoSky.WeatherService.dailyWeather.DailyWeather;
import com.KokoSky.WeatherService.dailyWeather.DailyWeatherDTO;
import com.KokoSky.WeatherService.dailyWeather.DailyWeatherId;
import com.KokoSky.WeatherService.exceptions.GeolocationException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeather;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeatherDTO;
import com.KokoSky.WeatherService.hourlyWeather.HourlyWeatherId;
import com.KokoSky.WeatherService.location.Location;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeather;
import com.KokoSky.WeatherService.realtimeWeather.RealtimeWeatherDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FullWeatherController.class)
public class FullWeatherControllerTest {

    private static final String END_POINT_PATH = "/api/v1/full";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FullWeatherService fullWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        GeolocationException ex = new GeolocationException("Geolocation error");
        when(geolocationService.getLocation(anyString())).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn404NotFound() throws Exception {
        Location location = Location.builder().code("DELHI_IN").build();

        when(geolocationService.getLocation(anyString())).thenReturn(location);

        LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
        when(fullWeatherService.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        RealtimeWeather realtimeWeather = RealtimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(new Date())
                .precipitation(88)
                .status("Cloudy")
                .windSpeed(5)
                .location(location) // Also sets locationCode in setter
                .build();

        location.setRealtimeWeather(realtimeWeather);

        DailyWeatherId id1 = DailyWeatherId
                .builder()
                .month(7)
                .dayOfMonth(16)
                .location(location)
                .build();

        DailyWeatherId id2 = DailyWeatherId
                .builder()
                .month(7)
                .dayOfMonth(17)
                .location(location)
                .build();

        DailyWeather dailyForecast1 = DailyWeather.builder()
                .id(id1)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        DailyWeather dailyForecast2 = DailyWeather.builder()
                .id(id2)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        location.setListDailyWeather(List.of(dailyForecast1, dailyForecast2));

        HourlyWeather hourlyForecast1 = HourlyWeather.builder()
                .id(HourlyWeatherId.builder().location(location).hourOfDay(10).build())
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeather hourlyForecast2 = HourlyWeather.builder()
                .id(HourlyWeatherId.builder().location(location).hourOfDay(11).build())
                .temperature(15)
                .precipitation(60)
                .status("Sunny")
                .build();

        location.setListHourlyWeather(List.of(hourlyForecast1, hourlyForecast2));

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(fullWeatherService.getByLocation(location)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "ABC123";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherService.get(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        RealtimeWeather realtimeWeather = RealtimeWeather.builder()
                .temperature(12)
                .humidity(32)
                .lastUpdated(new Date())
                .precipitation(88)
                .status("Cloudy")
                .windSpeed(5)
                .location(location)
                .build();

        location.setRealtimeWeather(realtimeWeather);

        DailyWeather dailyForecast1 = DailyWeather.builder()
                .id(DailyWeatherId.builder().location(location).month(7).dayOfMonth(16).build())
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        DailyWeather dailyForecast2 = DailyWeather.builder()
                .id(DailyWeatherId.builder().location(location).month(7).dayOfMonth(17).build())
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        location.setListDailyWeather(List.of(dailyForecast1, dailyForecast2));

        HourlyWeather hourlyForecast1 = HourlyWeather.builder()
                .id(HourlyWeatherId.builder().location(location).hourOfDay(10).build())
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeather hourlyForecast2 = HourlyWeather.builder()
                .id(HourlyWeatherId.builder().location(location).hourOfDay(11).build())
                .temperature(15)
                .precipitation(60)
                .status("Sunny")
                .build();

        location.setListHourlyWeather(List.of(hourlyForecast1, hourlyForecast2));

        when(fullWeatherService.get(locationCode)).thenReturn(location);

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoHourlyWeather() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        RealtimeWeatherDTO realtimeWeather = RealtimeWeatherDTO.builder()
                .status("Clear") // minimal valid field to pass validation
                .build();

        fullWeatherDTO.setRealtimeWeather(realtimeWeather);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Hourly weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoDailyWeather() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        RealtimeWeatherDTO realtimeWeather = RealtimeWeatherDTO.builder()
                .status("Clear") // minimal valid field to pass validation
                .build();

        fullWeatherDTO.setRealtimeWeather(realtimeWeather);

        HourlyWeatherDTO hourlyForecast1 = HourlyWeatherDTO.builder()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Daily weather data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidRealtimeWeatherData() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = DailyWeatherDTO
                .builder()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecast1);

        RealtimeWeatherDTO realtimeDTO = RealtimeWeatherDTO.builder().build();
        realtimeDTO.setTemperature(122);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Temperature must be in the range")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidHourlyWeatherData() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(100)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = DailyWeatherDTO
                .builder()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecast1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Hour of day must be in between 0-23")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidDailyWeatherData() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = DailyWeatherDTO
                .builder()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecast1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Status must be in between")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecast1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(33)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);

        DailyWeatherDTO dailyForecast1 = DailyWeatherDTO
                .builder()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecast1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(22);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(fullWeatherService.update(eq(locationCode), any())).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = new Location();
        location.setCode(locationCode);
        location.setCityName("New York City");
        location.setRegionName("New York");
        location.setCountryCode("US");
        location.setCountryName("United States of America");

        RealtimeWeather realtimeWeather = new RealtimeWeather();
        realtimeWeather.setTemperature(12);
        realtimeWeather.setHumidity(32);
        realtimeWeather.setLastUpdated(new Date());
        realtimeWeather.setPrecipitation(88);
        realtimeWeather.setStatus("Cloudy");
        realtimeWeather.setWindSpeed(5);

        location.setRealtimeWeather(realtimeWeather);

        DailyWeatherId dailyWeatherId = DailyWeatherId
                .builder()
                .location(location)
                .dayOfMonth(16)
                .month(7)
                .build();

        DailyWeather dailyForecast1 = DailyWeather
                .builder()
                .id(dailyWeatherId)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        location.setListDailyWeather(List.of(dailyForecast1));

        HourlyWeatherId id = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(10)
                .build();

        HourlyWeather hourlyForecast1 = HourlyWeather
                .builder()
                .id(id)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();


        location.setListHourlyWeather(List.of(hourlyForecast1));

        FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();

        HourlyWeatherDTO hourlyForecastDTO1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListHourlyWeather().add(hourlyForecastDTO1);

        DailyWeatherDTO dailyForecastDTO1 = DailyWeatherDTO
                .builder()
                .dayOfMonth(16)
                .month(7)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        fullWeatherDTO.getListDailyWeather().add(dailyForecastDTO1);

        RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
        realtimeDTO.setTemperature(12);
        realtimeDTO.setHumidity(32);
        realtimeDTO.setLastUpdated(new Date());
        realtimeDTO.setPrecipitation(88);
        realtimeDTO.setStatus("Cloudy");
        realtimeDTO.setWindSpeed(5);

        fullWeatherDTO.setRealtimeWeather(realtimeDTO);

        String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);

        when(fullWeatherService.update(eq(locationCode), any())).thenReturn(location);

        mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
                .andDo(print());
    }
}
