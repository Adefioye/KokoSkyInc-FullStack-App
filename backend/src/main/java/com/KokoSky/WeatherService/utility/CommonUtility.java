package com.KokoSky.WeatherService.utility;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtility.class);

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (isInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (isInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (isInvalid(ip)) {
            ip = request.getRemoteAddr();
        }

        // Handle multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // Convert IPv6 loopback to IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }

        LOGGER.info("Client's IP Address: {}", ip);

        return ip;
    }

    private static boolean isInvalid(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}
