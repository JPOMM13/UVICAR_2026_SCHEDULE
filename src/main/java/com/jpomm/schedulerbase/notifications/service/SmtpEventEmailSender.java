package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@ConditionalOnProperty(prefix = "uvicar.notifications.event-email", name = "delivery-mode", havingValue = "smtp")
public class SmtpEventEmailSender implements EventEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpEventEmailSender.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Lima"));

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String subjectTemplate;
    private final String smtpHost;
    private final int smtpPort;
    private final String smtpUsername;
    private final boolean smtpAuth;
    private final boolean startTlsEnabled;
    private final String smtpPassword;

    public SmtpEventEmailSender(
            final JavaMailSender mailSender,
            @Value("${uvicar.notifications.event-email.from:${spring.mail.username:}}") final String fromAddress,
            @Value("${uvicar.notifications.event-email.subject-template:[UVICAR] Evento detectado para unidad %s}") final String subjectTemplate,
            @Value("${spring.mail.host:}") final String smtpHost,
            @Value("${spring.mail.port:587}") final int smtpPort,
            @Value("${spring.mail.username:}") final String smtpUsername,
            @Value("${spring.mail.password:}") final String smtpPassword,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") final boolean smtpAuth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") final boolean startTlsEnabled
    ) {
        this.mailSender = Objects.requireNonNull(mailSender);
        this.fromAddress = fromAddress == null ? "" : fromAddress.trim();
        this.subjectTemplate = Objects.requireNonNull(subjectTemplate);
        this.smtpHost = smtpHost == null ? "" : smtpHost.trim();
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername == null ? "" : smtpUsername.trim();
        this.smtpPassword = smtpPassword == null ? "" : smtpPassword;
        this.smtpAuth = smtpAuth;
        this.startTlsEnabled = startTlsEnabled;
    }

    @PostConstruct
    void logConfiguration() {
        LOGGER.info("[event-email] SMTP sender enabled. host={} port={} username={} from={} auth={} startTls={} passwordConfigured={}",
                safeValue(smtpHost),
                smtpPort,
                maskEmail(smtpUsername),
                maskEmail(fromAddress),
                smtpAuth,
                startTlsEnabled,
                !smtpPassword.isBlank());
    }

    @Override
    public void send(final PendingEventEmailNotification notification) {
        LOGGER.info("[event-email] Sending SMTP email to={} unit={} eventCode={} via host={} port={} from={}",
                maskEmail(notification.recipientEmail()),
                notification.unitCode(),
                notification.triggeringEventCode(),
                safeValue(smtpHost),
                smtpPort,
                maskEmail(fromAddress));

        final SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isEmpty()) {
            message.setFrom(fromAddress);
        }
        message.setTo(notification.recipientEmail());
        message.setSubject(subjectFor(notification));
        message.setText(bodyFor(notification));
        mailSender.send(message);

        LOGGER.info("[event-email] SMTP email dispatched successfully to={} unit={}",
                maskEmail(notification.recipientEmail()),
                notification.unitCode());
    }

    private String subjectFor(final PendingEventEmailNotification notification) {
        return String.format(subjectTemplate, notification.unitCode());
    }

    private String bodyFor(final PendingEventEmailNotification notification) {
        final String occurredAt = notification.eventOccurredAt() == null
                ? "No informado"
                : DATE_TIME_FORMATTER.format(notification.eventOccurredAt().toInstant());

        return """
                Hola,

                Se detecto un evento pendiente de notificacion para el cliente %s.

                Unidad: %s
                Descripcion de unidad: %s
                Codigo de evento: %s
                Descripcion del evento: %s
                Fecha del evento: %s

                Este correo fue generado automaticamente por UVICAR Schedule.
                """.formatted(
                notification.clientName(),
                notification.unitCode(),
                notification.unitDescription(),
                notification.triggeringEventCode(),
                notification.triggeringEventDescription(),
                occurredAt
        );
    }

    private static String maskEmail(final String email) {
        if (email == null || email.isBlank()) {
            return "<empty>";
        }
        final int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***" + email.substring(Math.max(atIndex, 0));
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private static String safeValue(final String value) {
        return value == null || value.isBlank() ? "<empty>" : value;
    }
}
