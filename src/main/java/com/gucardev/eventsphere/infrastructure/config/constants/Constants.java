package com.gucardev.eventsphere.infrastructure.config.constants;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static String DEFAULT_AUDITOR;

    @Value("${app-specific-configs.constants.default-auditor}")
    private String defaultAuditor;

    @PostConstruct
    public void init() {
        DEFAULT_AUDITOR = defaultAuditor;
    }
}