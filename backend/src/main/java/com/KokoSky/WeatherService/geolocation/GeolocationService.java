package com.KokoSky.WeatherService.geolocation;

import com.KokoSky.WeatherService.exceptions.GeolocationException;
import com.KokoSky.WeatherService.location.Location;
import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeolocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);

    private final String DBPath = "IP2LOCATION-LITE-DB3.BIN";
    // Get the BIN file path from the classpath
    ClassPathResource resource = new ClassPathResource(DBPath);
    String dbPath = resource.getFile().getAbsolutePath();
    private final IP2Location ipLocator = new IP2Location();

    public GeolocationService() throws IOException {
        try {
            ipLocator.Open(dbPath);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public Location getLocation(String ipAddress) throws GeolocationException {

        try {
            IPResult result = ipLocator.IPQuery(ipAddress);

            if (!"OK".equals(result.getStatus())) {
                throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
            }

            LOGGER.info(result.toString());

            return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());

        } catch (IOException ex) {
            throw new GeolocationException("Error querying IP database", ex);
        }

    }
}
