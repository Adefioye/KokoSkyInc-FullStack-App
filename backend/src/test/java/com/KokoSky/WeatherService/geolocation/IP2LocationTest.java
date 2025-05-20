package com.KokoSky.WeatherService.geolocation;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IP2LocationTest {

    private String DBPath = "IP2LOCATION-LITE-DB3.BIN";

    @Test
    public void testInvalidIP() throws IOException {
        // Get the BIN file path from the classpath
        ClassPathResource resource = new ClassPathResource(DBPath);
        String dbPath = resource.getFile().getAbsolutePath();

        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(dbPath); // Use the resolved path

        String ipAddress = "abc";
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
        System.out.println(ipResult);
    }

    @Test
    public void testValidIP1() throws IOException {
        // Get the BIN file path from the classpath
        ClassPathResource resource = new ClassPathResource(DBPath);
        String dbPath = resource.getFile().getAbsolutePath();

        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(dbPath);

        String ipAddress = "108.30.178.78"; // New York
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("New York City");

        System.out.println(ipResult);
    }

    @Test
    public void testValidIP2() throws IOException {
        // Get the BIN file path from the classpath
        ClassPathResource resource = new ClassPathResource(DBPath);
        String dbPath = resource.getFile().getAbsolutePath();

        IP2Location ipLocator = new IP2Location();
        ipLocator.Open(dbPath);

        String ipAddress = "103.48.198.141"; // Delhi
        IPResult ipResult = ipLocator.IPQuery(ipAddress);

        assertThat(ipResult.getStatus()).isEqualTo("OK");
        assertThat(ipResult.getCity()).isEqualTo("Delhi");

        System.out.println(ipResult);
    }


}
