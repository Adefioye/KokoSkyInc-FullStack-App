package com.KokoSky.WeatherService.realtimeWeather;

import com.KokoSky.WeatherService.exceptions.GeolocationException;
import com.KokoSky.WeatherService.exceptions.LocationNotFoundException;
import com.KokoSky.WeatherService.geolocation.GeolocationService;
import com.KokoSky.WeatherService.location.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RealtimeWeatherController.class)
public class RealtimeWeatherControllerTest {

    @MockBean
    private RealtimeWeatherService realtimeWeatherService;

    @MockBean
    private GeolocationService geolocationService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String END_POINT_PATH = "/api/v1/realtime";

    @Test
    public void testGETShouldReturnBadRequest_with400StatusCode() throws Exception {
        when(geolocationService.getLocation(anyString())).thenThrow(GeolocationException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testGETShouldReturnNotFound_with404StatusCode_whenLocationNotFound() throws Exception {
        Location location = new Location();

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(realtimeWeatherService.getByLocation(location)).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGETShouldReturnSuccess_with200StatusCode() throws Exception {
        // Given
        String locationCode = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;


        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        RealtimeWeather realTimeWeather = RealtimeWeather
                .builder()
                .temperature(75)
                .humidity(50)
                .precipitation(1015)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        when(geolocationService.getLocation(anyString())).thenReturn(location);
        when(realtimeWeatherService.getByLocation(location)).thenReturn(realTimeWeather);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(get(END_POINT_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location").value(expectedLocation))
                .andDo(print());
    }

    @Test
    public void testGetbyLocationCodeShouldReturnNotFound_with404StatusCode() throws Exception {
        String locationCode = "ABC_US";

        when(realtimeWeatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);

        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(get(requestURI))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testGetByLocationCodeShouldReturnSuccess_with200StatusCode() throws Exception {
        // Given
        String locationCode = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;


        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        RealtimeWeather realTimeWeather = RealtimeWeather
                .builder()
                .temperature(75)
                .humidity(50)
                .precipitation(1015)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        // Set location on realtimeWeather
        location.setRealtimeWeather(realTimeWeather);
        realTimeWeather.setLocation(location);

        when(realtimeWeatherService.getByLocationCode(locationCode)).thenReturn(realTimeWeather);

        String requestURI = END_POINT_PATH + "/" + locationCode;
        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();

        mockMvc.perform(get(requestURI))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.location").value(expectedLocation))
                .andExpect(jsonPath("$.temperature").value(75))
                .andExpect(jsonPath("$.status").value("Snowy"))
                .andDo(print());
    }

    @Test
    public void testUpdateRealtimeWeatherByLocationCode_returnBadRequest_with400StatusCode() throws Exception {
        String locationCode = "ABC_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealtimeWeather realtimeWeather = RealtimeWeather
                .builder()
                .temperature(75)
                .humidity(50)
                .precipitation(1015)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        String bodyContent = objectMapper.writeValueAsString(realtimeWeather);

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testUpdateRealtimeWeatherByLocationCode_returnNotFound_with404StatusCode() throws Exception {
        String locationCode = "LACA_US";
        String requestURI = END_POINT_PATH + "/" + locationCode;

        RealtimeWeatherDTO dto = RealtimeWeatherDTO
                .builder()
                .temperature(12)
                .humidity(32)
                .precipitation(88)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        String bodyContent = objectMapper.writeValueAsString(dto);

        when(realtimeWeatherService.update(eq(locationCode), any(RealtimeWeather.class))).thenThrow(LocationNotFoundException.class);

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testUpdateRealtimeWeatherByLocationCodeIsSuccesful_with200StatusCode() throws Exception {
        // Given

        String locationCode = "LACA_US";
        String cityName = "Los Angeles";
        String regionName = "California";
        String countryName = "United States Of America";
        String countryCode = "US";
        boolean enabled = true;
        boolean trashed = false;


        Location location = Location
                .builder()
                .code(locationCode)
                .cityName(cityName)
                .regionName(regionName)
                .countryName(countryName)
                .countryCode(countryCode)
                .enabled(enabled)
                .trashed(trashed)
                .build();

        RealtimeWeather realtimeWeather = RealtimeWeather
                .builder()
                .temperature(12)
                .humidity(32)
                .precipitation(88)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();

        RealtimeWeatherDTO dto = RealtimeWeatherDTO
                .builder()
                .location(location.toString())
                .temperature(12)
                .humidity(32)
                .precipitation(88)
                .windSpeed(57)
                .status("Snowy")
                .lastUpdated(new Date())
                .build();


        // Set location on realtimeWeather
        location.setRealtimeWeather(realtimeWeather);
        realtimeWeather.setLocation(location);

        when(realtimeWeatherService.update(anyString(), any())).thenReturn(realtimeWeather);

        String bodyContent = objectMapper.writeValueAsString(dto);

        String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
        String requestURI = END_POINT_PATH + "/" + locationCode;

        mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", is(expectedLocation)))
                .andDo(print());
    }

}
