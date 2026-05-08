package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import com.jpomm.schedulerbase.notifications.repository.EventEmailNotificationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventEmailNotificationServiceImplTest {

    @Test
    void shouldListPendingNotificationsByRazTraWithoutSendingEmails() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        final List<PendingEventEmailNotification> pendingNotifications = List.of(
                notification("john.manchego.medina@gmail.com", "424722", "Energia Principal desconectada")
        );
        when(repository.findPendingNotifications(44)).thenReturn(pendingNotifications);

        final List<PendingEventEmailNotification> result = service.listPendingNotifications(44);

        assertEquals(pendingNotifications, result);
        verify(repository).findPendingNotifications(44);
        verify(sender, never()).send(org.mockito.ArgumentMatchers.any(PendingEventEmailNotification.class));
    }

    @Test
    void shouldDispatchAllPendingNotifications() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        final List<PendingEventEmailNotification> pendingNotifications = List.of(
                notification("john.manchego.medina@gmail.com", "424722", "Energia Principal desconectada"),
                notification("john.manchego.medina@gmail.com", "B5J969", "Energia Principal desconectada")
        );
        when(repository.findPendingNotifications(44)).thenReturn(pendingNotifications);

        final EventEmailJobResult result = service.processPendingNotifications(44);

        assertEquals(2, result.fetchedCount());
        assertEquals(2, result.sentCount());
        verify(sender, times(2)).send(org.mockito.ArgumentMatchers.any(PendingEventEmailNotification.class));
    }

    @Test
    void shouldReturnZeroWhenThereAreNoPendingNotifications() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        when(repository.findPendingNotifications(44)).thenReturn(List.of());

        final EventEmailJobResult result = service.processPendingNotifications(44);

        assertEquals(0, result.fetchedCount());
        assertEquals(0, result.sentCount());
        verify(sender, times(0)).send(org.mockito.ArgumentMatchers.any(PendingEventEmailNotification.class));
    }

    @Test
    void shouldContinueWhenOneNotificationFailsToSend() {
        final EventEmailNotificationRepository repository = mock(EventEmailNotificationRepository.class);
        final EventEmailSender sender = mock(EventEmailSender.class);
        final EventEmailNotificationServiceImpl service = new EventEmailNotificationServiceImpl(repository, sender);

        final PendingEventEmailNotification failingNotification =
                notification("correo-invalido", "424722", "Energia Principal desconectada");
        final PendingEventEmailNotification validNotification =
                notification("john.manchego.medina@gmail.com", "B5J969", "Energia Principal desconectada");
        when(repository.findPendingNotifications(44)).thenReturn(List.of(failingNotification, validNotification));
        doThrow(new IllegalArgumentException("Invalid address")).when(sender).send(failingNotification);

        final EventEmailJobResult result = service.processPendingNotifications(44);

        assertEquals(2, result.fetchedCount());
        assertEquals(1, result.sentCount());
        verify(sender).send(failingNotification);
        verify(sender).send(validNotification);
    }

    private static PendingEventEmailNotification notification(final String email, final String plate, final String event) {
        return new PendingEventEmailNotification(
                email,
                "Cliente Demo",
                plate,
                event,
                "-12.58427660",
                "-76.66905830",
                "LIMA / CANETE / SANTA CRUZ DE FLORES -"
        );
    }
}
