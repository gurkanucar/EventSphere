package com.gucardev.eventsphere.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/healthcheck")
public class HelloWorldController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> hello() {
        log.info("Health Check endpoint called");
        log.info("this is test log");

        try {
            ZonedDateTime now = ZonedDateTime.now();
            ZoneId systemZone = ZoneId.systemDefault();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Hello World!");
            response.put("status", "success");

            // Time and timezone information
            response.put("timestamp", now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
            response.put("localTime", now.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("epochSeconds", now.toEpochSecond());
            response.put("timezone", systemZone.toString());
            response.put("timezoneOffset", now.getOffset().toString());

            // System information
            response.put("javaVersion", System.getProperty("java.version"));

            // Application information
            response.put("environment", System.getProperty("spring.profiles.active", "default"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error in hello endpoint", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Hello World!");
            errorResponse.put("status", "error");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return ResponseEntity.ok(errorResponse);
        }
    }
}