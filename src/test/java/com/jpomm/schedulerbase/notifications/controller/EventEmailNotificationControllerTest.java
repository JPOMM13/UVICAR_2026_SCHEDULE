package com.jpomm.schedulerbase.notifications.controller;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.EventEmailDispatchRequest;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.facade.EventEmailNotificationFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventEmailNotificationControllerTest {

    @Mock
    private EventEmailNotificationFacade facade;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private EventEmailNotificationController controller;

    @Test
    void listPendingNotificationsReturnsFacadeResponseForQueryRazTra() {
        final List<PendingEventEmailNotification> expected = List.of(new PendingEventEmailNotification(
                "cliente@correo.com",
                "Cliente Demo",
                "424722",
                "Energia Principal desconectada",
                "-12.58427660",
                "-76.66905830",
                "LIMA / CANETE / SANTA CRUZ DE FLORES -"
        ));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/notifications/event-emails/pending");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(facade.listPendingNotifications(44)).thenReturn(expected);

        final List<PendingEventEmailNotification> actual = controller.listPendingNotifications(44, request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listPendingNotifications(44);
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }

    @Test
    void dispatchPendingNotificationsReturnsFacadeResponseForBodyRazTra() {
        final EventEmailJobResult expected = new EventEmailJobResult(6, 6);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/notifications/event-emails/dispatch");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(facade.processPendingNotifications(44)).thenReturn(expected);

        final EventEmailJobResult actual = controller.dispatchPendingNotifications(new EventEmailDispatchRequest(44), request);

        assertThat(actual).isSameAs(expected);
        verify(facade).processPendingNotifications(44);
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }
}
