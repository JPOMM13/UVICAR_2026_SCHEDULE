package com.jpomm.schedulerbase.notifications.repository;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class EventEmailNotificationRepository {

    private static final Pattern SAFE_PROC = Pattern.compile("^[A-Za-z0-9_\\.\\[\\]]+$");

    private final JdbcTemplate jdbcTemplate;
    private final String storedProcedureName;
    private final boolean mockEnabled;

    public EventEmailNotificationRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.notifications.event-email.sp:pa_NotificacionesEventosPendientesCorreo}")
            final String storedProcedureName,
            @Value("${uvicar.notifications.event-email.mock-enabled:true}")
            final boolean mockEnabled
    ) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.storedProcedureName = validateStoredProcedureName(storedProcedureName);
        this.mockEnabled = mockEnabled;
    }

    public List<PendingEventEmailNotification> findPendingNotifications() {
        if (mockEnabled) {
            return mockNotifications();
        }

        final String sql = "EXEC " + storedProcedureName;
        return jdbcTemplate.queryForList(sql)
                .stream()
                .map(this::mapRow)
                .toList();
    }

    private PendingEventEmailNotification mapRow(final Map<String, Object> row) {
        return new PendingEventEmailNotification(
                stringValue(row, "correo", "email", "recipientEmail"),
                stringValue(row, "cliente", "clientName", "razonSocial"),
                stringValue(row, "unidad", "unitCode", "codUnidad"),
                stringValue(row, "unidadDescripcion", "unitDescription", "placa"),
                stringValue(row, "evento", "eventCode", "triggeringEventCode"),
                stringValue(row, "eventoDescripcion", "eventDescription", "triggeringEventDescription"),
                dateValue(row, "fechaEvento", "eventOccurredAt", "fecha")
        );
    }

    private List<PendingEventEmailNotification> mockNotifications() {
        final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return List.of(
                new PendingEventEmailNotification(
                        "john.manchego.medina@gmail.com",
                        "Transportes Acme",
                        "UNI-24794",
                        "Volvo FH 540",
                        "SIN_GPS",
                        "Unidad sin comunicacion GPS",
                        now.minusMinutes(17)
                ),
                new PendingEventEmailNotification(
                        "john.manchego.medina@gmail.com",
                        "Contoso Logistics",
                        "UNI-32019",
                        "Scania R450",
                        "BATERIA_BAJA",
                        "Bateria por debajo del umbral",
                        now.minusMinutes(52)
                )
        );
    }

    private static String validateStoredProcedureName(final String name) {
        final String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty() || !SAFE_PROC.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid stored procedure name for uvicar.notifications.event-email.sp");
        }
        return trimmed;
    }

    private static String stringValue(final Map<String, Object> row, final String... keys) {
        for (final String key : keys) {
            final Object value = row.get(key);
            if (value != null) {
                return String.valueOf(value);
            }
        }
        return "";
    }

    private static OffsetDateTime dateValue(final Map<String, Object> row, final String... keys) {
        for (final String key : keys) {
            final Object value = row.get(key);
            if (value instanceof OffsetDateTime offsetDateTime) {
                return offsetDateTime;
            }
            if (value instanceof Timestamp timestamp) {
                return timestamp.toInstant().atOffset(ZoneOffset.UTC);
            }
        }
        return null;
    }
}
