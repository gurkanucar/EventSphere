package com.gucardev.eventsphere.infrastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app-specific-configs")
public class AppSpecificProperties {
    private List<String> ignoredLoggingPaths;
}