package com.gucardev.eventsphere.infrastructure.config.email;

public record HtmlEmailRequest(String subject, String to, String templateName) {
}
