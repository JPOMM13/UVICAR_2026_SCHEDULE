package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpEventEmailSenderTest {

    @Test
    void shouldBuildAndSendExpectedMessage() {
        final JavaMailSender mailSender = mock(JavaMailSender.class);
        final SmtpEventEmailSender sender = new SmtpEventEmailSender(
                mailSender,
                "john.manchego.medina@gmail.com",
                "[UVICAR] Evento detectado para unidad %s",
                "smtp.gmail.com",
                587,
                "john.manchego.medina@gmail.com",
                "app-password",
                true,
                true
        );

        final PendingEventEmailNotification notification = new PendingEventEmailNotification(
                "john.manchego.medina@gmail.com",
                "Transportes Acme",
                "UNI-24794",
                "Volvo FH 540",
                "SIN_GPS",
                "Unidad sin comunicacion GPS",
                OffsetDateTime.of(2026, 4, 7, 12, 30, 0, 0, ZoneOffset.UTC)
        );

        sender.send(notification);

        final ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        final SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("john.manchego.medina@gmail.com", sentMessage.getFrom());
        assertEquals("john.manchego.medina@gmail.com", sentMessage.getTo()[0]);
        assertEquals("[UVICAR] Evento detectado para unidad UNI-24794", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Transportes Acme"));
        assertTrue(sentMessage.getText().contains("SIN_GPS"));
        assertTrue(sentMessage.getText().contains("UNI-24794"));
    }
}
