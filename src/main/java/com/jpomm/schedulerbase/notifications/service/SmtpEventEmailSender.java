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

import java.util.List;
import java.util.Objects;

@Component
@ConditionalOnProperty(prefix = "uvicar.notifications.event-email", name = "delivery-mode", havingValue = "smtp")
public class SmtpEventEmailSender implements EventEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpEventEmailSender.class);

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
        final List<String> recipients = notification.recipientEmails();
        if (recipients.isEmpty()) {
            throw new IllegalArgumentException("No recipient email configured for plate " + notification.plate());
        }

        LOGGER.info("[event-email] Sending SMTP email to={} plate={} event={} via host={} port={} from={}",
                maskEmails(recipients),
                notification.plate(),
                notification.event(),
                safeValue(smtpHost),
                smtpPort,
                maskEmail(fromAddress));

        final SimpleMailMessage message = new SimpleMailMessage();
        if (!fromAddress.isEmpty()) {
            message.setFrom(fromAddress);
        }
        message.setTo(recipients.toArray(String[]::new));
        message.setSubject(subjectFor(notification));
        message.setText(bodyFor(notification));
        mailSender.send(message);

        LOGGER.info("[event-email] SMTP email dispatched successfully to={} plate={}",
                maskEmails(recipients),
                notification.plate());
    }

    private String subjectFor(final PendingEventEmailNotification notification) {
        return String.format(subjectTemplate, notification.plate());
    }

    private String bodyFor(final PendingEventEmailNotification notification) {
        final String mapsUrl = notification.googleMapsUrl();
        final String mapsLine = mapsUrl.isBlank() ? "" : "\nMapa: " + mapsUrl;

        return """
                Estimado cliente,

                Le informamos que se ha detectado el siguiente evento en una de sus unidades:

                Cliente: %s
                Placa: %s
                Evento: %s
                Ubicacion: %s
                Coordenadas: %s, %s%s

                Este correo fue generado automaticamente por UVICAR Schedule.
                """.formatted(
                notification.clientName(),
                notification.plate(),
                notification.event(),
                blankAsNotReported(notification.location()),
                blankAsNotReported(notification.latitude()),
                blankAsNotReported(notification.longitude()),
                mapsLine
        );
    }

    private static String blankAsNotReported(final String value) {
        return value == null || value.isBlank() ? "No informado" : value;
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

    private static String maskEmails(final List<String> emails) {
        return emails.stream()
                .map(SmtpEventEmailSender::maskEmail)
                .toList()
                .toString();
    }

    private static String safeValue(final String value) {
        return value == null || value.isBlank() ? "<empty>" : value;
    }
}
