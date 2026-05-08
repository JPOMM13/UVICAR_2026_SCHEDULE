package com.jpomm.schedulerbase.notifications.controller;

import com.jpomm.schedulerbase.notifications.dto.EventEmailDispatchRequest;
import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.facade.EventEmailNotificationFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public List<PendingEventEmailNotification> listPendingNotifications(
            @RequestParam("nRazTra") final Integer razTra,
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        if (razTra == null) {
            throw new ResponseStatusException(BAD_REQUEST, "nRazTra is required");
        }
        return facade.listPendingNotifications(razTra);
    }

    @PostMapping("/dispatch")
    public EventEmailJobResult dispatchPendingNotifications(
            @RequestBody final EventEmailDispatchRequest dispatchRequest,
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        if (dispatchRequest == null || dispatchRequest.nRazTra() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "nRazTra is required");
        }
        return facade.processPendingNotifications(dispatchRequest.nRazTra());
    }
}
