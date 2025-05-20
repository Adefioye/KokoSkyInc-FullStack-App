package com.KokoSky.WeatherService.dailyWeather;

import com.KokoSky.WeatherService.exceptions.GeolocationException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DailyWeatherController.class)
public class DailyWeatherControllerTest {

    private static final String END_POINT_PATH = "/api/v1/daily";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DailyWeatherService dailyWeatherService;

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
        when(dailyWeatherService.getByLocation(location)).thenThrow(ex);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn204NoContent() throws Exception {
        Location location = Location.builder().code("DELHI_IN").build();

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherService.getByLocation(location)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByIPShouldReturn200OK() throws Exception {
        // Arrange – Create Location
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        // Create DailyWeatherId instances
        DailyWeatherId id1 = new DailyWeatherId(16, 7, location);
        DailyWeatherId id2 = new DailyWeatherId(17, 7, location);

        // Create DailyWeather entries with ID
        DailyWeather forecast1 = DailyWeather.builder()
                .id(id1)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        DailyWeather forecast2 = DailyWeather.builder()
                .id(id2)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        // Mock service calls
        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));

        // Act & Assert
        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andExpect(jsonPath("$.daily_forecast[0].status", is("Cloudy")))
                .andExpect(jsonPath("$.daily_forecast[1].day_of_month", is(17)))
                .andExpect(jsonPath("$.daily_forecast[1].status", is("Sunny")))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn404NotFound() throws Exception {
        String locationCode = "LACA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ex.getMessage())))
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn204NoContent() throws Exception {
        String locationCode = "LACA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(new ArrayList<>());

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetByCodeShouldReturn200OK() throws  Exception {
        String requestURI = END_POINT_PATH + "/" + "NYC_USA";
        // Arrange – Create Location
        Location location = Location.builder()
                .code("NYC_USA")
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        // Create DailyWeatherId instances
        DailyWeatherId id1 = new DailyWeatherId(16, 7, location);
        DailyWeatherId id2 = new DailyWeatherId(17, 7, location);

        // Create DailyWeather entries with ID
        DailyWeather forecast1 = DailyWeather.builder()
                .id(id1)
                .minTemp(23)
                .maxTemp(32)
                .precipitation(40)
                .status("Cloudy")
                .build();

        DailyWeather forecast2 = DailyWeather.builder()
                .id(id2)
                .minTemp(25)
                .maxTemp(34)
                .precipitation(30)
                .status("Sunny")
                .build();

        when(dailyWeatherService.getByLocationCode("NYC_USA")).thenReturn(List.of(forecast1, forecast2));

        String expectedLocation = location.toString();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
        String requestURI = END_POINT_PATH + "/NYC_USA";

        List<DailyWeatherDTO> listDTO = Collections.emptyList();

        String requestBody = objectMapper.writeValueAsString(listDTO);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Daily forecast data cannot be empty")))
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn404NotFound() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        DailyWeatherDTO dto = DailyWeatherDTO.builder()
                .dayOfMonth(21)
                .month(7)
                .minTemp(23)
                .maxTemp(30)
                .precipitation(20)
                .status("Clear")
                .build();

        List<DailyWeatherDTO> listDTO = List.of(dto);

        String requestBody = objectMapper.writeValueAsString(listDTO);

        LocationNotFoundException ex = new LocationNotFoundException(locationCode);
        when(dailyWeatherService.updateByLocationCode(eq(locationCode), anyList())).thenThrow(ex);

        mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateShouldReturn200OK() throws Exception {
        String locationCode = "NYC_USA";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        // Request DTOs
        DailyWeatherDTO dto1 = DailyWeatherDTO.builder()
                .dayOfMonth(17)
                .month(7)
                .minTemp(25)
                .maxTemp(35)
                .precipitation(40)
                .status("Sunny")
                .build();

        DailyWeatherDTO dto2 = DailyWeatherDTO.builder()
                .dayOfMonth(18)
                .month(7)
                .minTemp(26)
                .maxTemp(34)
                .precipitation(50)
                .status("Clear")
                .build();

        // Build Location using Lombok builder
        Location location = Location.builder()
                .code(locationCode)
                .cityName("New York City")
                .regionName("New York")
                .countryCode("US")
                .countryName("United States of America")
                .build();

        // Build DailyWeather using builder with embedded id
        DailyWeather forecast1 = DailyWeather.builder()
                .id(DailyWeatherId.builder()
                        .dayOfMonth(17)
                        .month(7)
                        .location(location)
                        .build())
                .minTemp(25)
                .maxTemp(35)
                .precipitation(40)
                .status("Sunny")
                .build();

        DailyWeather forecast2 = DailyWeather.builder()
                .id(DailyWeatherId.builder()
                        .dayOfMonth(18)
                        .month(7)
                        .location(location)
                        .build())
                .minTemp(26)
                .maxTemp(34)
                .precipitation(50)
                .status("Clear")
                .build();

        List<DailyWeatherDTO> listDTO = List.of(dto1, dto2);
        List<DailyWeather> dailyForecast = List.of(forecast1, forecast2);
        String requestBody = objectMapper.writeValueAsString(listDTO);

        when(dailyWeatherService.updateByLocationCode(eq(locationCode), anyList()))
                .thenReturn(dailyForecast);

        mockMvc.perform(put(requestURI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(location.toString())))
                .andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(17)))
                .andDo(print());
    }

}
