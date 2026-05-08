package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
                "[UVICAR] Alerta de evento para placa %s",
                "smtp.gmail.com",
                587,
                "john.manchego.medina@gmail.com",
                "app-password",
                true,
                true
        );

        final PendingEventEmailNotification notification = new PendingEventEmailNotification(
                "john.manchego.medina@gmail.com,leslie@laencontre.com",
                "AGROINDUSTRIAS SAN ANDRES S.A.C.",
                "424722",
                "Energia Principal desconectada",
                "-12.58427660",
                "-76.66905830",
                "LIMA / CANETE / SANTA CRUZ DE FLORES -"
        );

        sender.send(notification);

        final ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        final SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("john.manchego.medina@gmail.com", sentMessage.getFrom());
        assertArrayEquals(new String[]{"john.manchego.medina@gmail.com", "leslie@laencontre.com"}, sentMessage.getTo());
        assertEquals("[UVICAR] Alerta de evento para placa 424722", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("AGROINDUSTRIAS SAN ANDRES S.A.C."));
        assertTrue(sentMessage.getText().contains("Energia Principal desconectada"));
        assertTrue(sentMessage.getText().contains("424722"));
        assertTrue(sentMessage.getText().contains("LIMA / CANETE / SANTA CRUZ DE FLORES -"));
        assertTrue(sentMessage.getText().contains("https://www.google.com/maps?q=-12.58427660,-76.66905830"));
    }
}
