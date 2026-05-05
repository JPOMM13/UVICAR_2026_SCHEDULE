package com.jpomm.schedulerbase.notifications.controller;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.facade.EventEmailNotificationFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/notifications/event-emails")
public class EventEmailNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventEmailNotificationController.class);

    private final EventEmailNotificationFacade facade;

    public EventEmailNotificationController(final EventEmailNotificationFacade facade) {
        this.facade = Objects.requireNonNull(facade);
    }

    @GetMapping("/pending")
    public List<PendingEventEmailNotification> listPendingNotifications(final HttpServletRequest request) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return facade.listPendingNotifications();
    }

    @PostMapping("/dispatch")
    public EventEmailJobResult dispatchPendingNotifications(final HttpServletRequest request) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return facade.processPendingNotifications();
    }
}
