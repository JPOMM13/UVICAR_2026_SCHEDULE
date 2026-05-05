package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "uvicar.notifications.event-email", name = "delivery-mode", havingValue = "log", matchIfMissing = true)
public class LoggingEventEmailSender implements EventEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingEventEmailSender.class);

    @Override
    public void send(final PendingEventEmailNotification notification) {
        LOGGER.info("[event-email] Mock send to={} client={} unit={} eventCode={} eventDescription={} occurredAt={}",
                notification.recipientEmail(),
                notification.clientName(),
                notification.unitCode(),
                notification.triggeringEventCode(),
                notification.triggeringEventDescription(),
                notification.eventOccurredAt());
    }
}
