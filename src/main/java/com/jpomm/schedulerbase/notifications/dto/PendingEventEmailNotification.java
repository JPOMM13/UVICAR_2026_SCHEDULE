package com.jpomm.schedulerbase.notifications.dto;

import java.util.Arrays;
import java.util.List;

public record PendingEventEmailNotification(
        String recipientEmail,
        String clientName,
        String plate,
        String event,
        String latitude,
        String longitude,
        String location
) {

    public List<String> recipientEmails() {
        if (isBlank(recipientEmail)) {
            return List.of();
        }
        return Arrays.stream(recipientEmail.split("[,;\\s]+"))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .toList();
    }

    public String googleMapsUrl() {
        if (isBlank(latitude) || isBlank(longitude)) {
            return "";
        }
        return "https://www.google.com/maps?q=" + latitude.trim() + "," + longitude.trim();
    }

    private static boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}
