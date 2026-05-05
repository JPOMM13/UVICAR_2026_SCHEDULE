package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.repository.EventEmailNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EventEmailNotificationServiceImpl implements EventEmailNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventEmailNotificationServiceImpl.class);

    private final EventEmailNotificationRepository repository;
    private final EventEmailSender emailSender;

    public EventEmailNotificationServiceImpl(
            final EventEmailNotificationRepository repository,
            final EventEmailSender emailSender
    ) {
        this.repository = Objects.requireNonNull(repository);
        this.emailSender = Objects.requireNonNull(emailSender);
    }

    @Override
    public List<PendingEventEmailNotification> listPendingNotifications() {
        return repository.findPendingNotifications();
    }

    @Override
    public EventEmailJobResult processPendingNotifications() {
        final List<PendingEventEmailNotification> pendingNotifications = repository.findPendingNotifications();
        if (pendingNotifications.isEmpty()) {
            LOGGER.info("[event-email] No pending notifications found.");
            return new EventEmailJobResult(0, 0);
        }

        int sentCount = 0;
        for (final PendingEventEmailNotification notification : pendingNotifications) {
            emailSender.send(notification);
            sentCount++;
        }

        LOGGER.info("[event-email] Processed {} pending notifications.", sentCount);
        return new EventEmailJobResult(pendingNotifications.size(), sentCount);
    }
}
