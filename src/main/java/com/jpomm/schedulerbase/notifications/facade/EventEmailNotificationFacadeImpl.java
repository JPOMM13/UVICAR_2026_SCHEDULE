package com.jpomm.schedulerbase.notifications.facade;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.service.EventEmailNotificationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class EventEmailNotificationFacadeImpl implements EventEmailNotificationFacade {

    private final EventEmailNotificationService service;

    public EventEmailNotificationFacadeImpl(final EventEmailNotificationService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public List<PendingEventEmailNotification> listPendingNotifications() {
        return service.listPendingNotifications();
    }

    @Override
    public EventEmailJobResult processPendingNotifications() {
        return service.processPendingNotifications();
    }
}
