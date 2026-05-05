package com.jpomm.schedulerbase.notifications.dto;

import java.time.OffsetDateTime;

public record PendingEventEmailNotification(
        String recipientEmail,
        String clientName,
        String unitCode,
        String unitDescription,
        String triggeringEventCode,
        String triggeringEventDescription,
        OffsetDateTime eventOccurredAt
) {
}
