package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.repository.EventEmailNotificationRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventEmailNotificationServiceImplTest {

    @Test
    void shouldDispatchAllPendingNotifications() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        final List<PendingEventEmailNotification> pendingNotifications = List.of(
                notification("john.manchego.medina@gmail.com", "UNI-24794", "SIN_GPS"),
                notification("john.manchego.medina@gmail.com", "UNI-32019", "BATERIA_BAJA")
        );
        when(repository.findPendingNotifications()).thenReturn(pendingNotifications);

        final EventEmailJobResult result = service.processPendingNotifications();

        assertEquals(2, result.fetchedCount());
        assertEquals(2, result.sentCount());
        verify(sender, times(2)).send(org.mockito.ArgumentMatchers.any(PendingEventEmailNotification.class));
    }

    @Test
    void shouldReturnZeroWhenThereAreNoPendingNotifications() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        when(repository.findPendingNotifications()).thenReturn(List.of());

        final EventEmailJobResult result = service.processPendingNotifications();

        assertEquals(0, result.fetchedCount());
        assertEquals(0, result.sentCount());
        verify(sender, times(0)).send(org.mockito.ArgumentMatchers.any(PendingEventEmailNotification.class));
    }

    private static PendingEventEmailNotification notification(final String email, final String unitCode, final String eventCode) {
        return new PendingEventEmailNotification(
                email,
                "Cliente Demo",
                unitCode,
                "Unidad Demo",
                eventCode,
                "Evento Demo",
                OffsetDateTime.of(2026, 4, 7, 12, 30, 0, 0, ZoneOffset.UTC)
        );
    }
}
