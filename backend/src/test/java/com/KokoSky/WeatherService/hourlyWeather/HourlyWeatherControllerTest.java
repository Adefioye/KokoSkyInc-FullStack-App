package com.KokoSky.WeatherService.hourlyWeather;

import com.KokoSky.WeatherService.exceptions.GeolocationException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(HourlyWeatherController.class)
public class HourlyWeatherControllerTest {

    private static final String X_CURRENT_HOUR = "X-Current-Hour";

    private static final String END_POINT_PATH = "/api/v1/hourly";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HourlyWeatherService hourlyWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
        when(geolocationService.getLocation(Mockito.anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        Location location = Location.builder().code("DELHI_IN").build();

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        int currentHour = 9;
        Location location = Location
                .builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        HourlyWeatherId hourlyWeatherId1 = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(10)
                .build();

        HourlyWeatherId hourlyWeatherId2 = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(11)
                .build();

        HourlyWeather forecast1 = HourlyWeather
                .builder()
                .id(hourlyWeatherId1)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeather forecast2 = HourlyWeather
                .builder()
                .id(hourlyWeatherId2)
                .temperature(15)
                .precipitation(60)
                .status("Sunny")
                .build();

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andExpect(jsonPath("$.hourly_forecast[1].hour_of_day", is(11)))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn404NotFound() throws Exception {
        Location location = Location.builder().code("DELHI_IN").build();
        int currentHour = 9;

        when(geolocationService.getLocation(Mockito.anyString())).thenReturn(location);
        when(hourlyWeatherService.getByLocation(location, currentHour)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn400BadRequest() throws Exception {
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn404NotFound() throws Exception {
        int currentHour = 9;
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn204NoContent() throws Exception {
        int currentHour = 9;
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturn200OK() throws Exception {
        int currentHour = 9;
        String locationCode = "DELHI_IN";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        Location location = Location
                .builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        HourlyWeatherId hourlyWeatherId1 = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(10)
                .build();

        HourlyWeatherId hourlyWeatherId2 = HourlyWeatherId
                .builder()
                .location(location)
                .hourOfDay(11)
                .build();

        HourlyWeather forecast1 = HourlyWeather
                .builder()
                .id(hourlyWeatherId1)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeather forecast2 = HourlyWeather
                .builder()
                .id(hourlyWeatherId2)
                .temperature(15)
                .precipitation(60)
                .status("Sunny")
                .build();

        var hourlyForecast = List.of(forecast1, forecast2);

        when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(hourlyForecast);

        mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
                .andDo(print());
    }

    @Test
    public void testUpdateByLocationCodeShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        List<HourlyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Hourly forecast data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        HourlyWeatherDTO dto1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(133)
                .precipitation(70)
                .status("Cloudy")
                .build();

        HourlyWeatherDTO dto2 = HourlyWeatherDTO
                .builder()
                .hourOfDay(11)
                .temperature(15)
                .precipitation(120)
                .status("Sunny")
                .build();

        List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*]").value(
                        containsInAnyOrder(
                                containsString("Temperature must be in the range"),
                                containsString("Precipitation must be in the range of 0 to 100 percentage")
                        )))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        HourlyWeatherDTO dto1 = HourlyWeatherDTO
                .builder()
                .hourOfDay(10)
                .temperature(13)
                .precipitation(70)
                .status("Cloudy")
                .build();

        List<HourlyWeatherDTO> listDTO = List.of(dto1);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
                .thenThrow(LocationNotFoundException.class);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
