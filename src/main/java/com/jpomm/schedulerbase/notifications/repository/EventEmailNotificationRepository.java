package com.jpomm.schedulerbase.notifications.repository;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class EventEmailNotificationRepository {

    private static final Pattern SAFE_PROC = Pattern.compile("^[A-Za-z0-9_\\.\\[\\]]+$");

    private final JdbcTemplate jdbcTemplate;
    private final String storedProcedureName;

    public EventEmailNotificationRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.notifications.event-email.sp:pa_envioCorreosEventosAlertas_1Hora}")
            final String storedProcedureName
    ) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.storedProcedureName = validateStoredProcedureName(storedProcedureName);
    }

    public List<PendingEventEmailNotification> findPendingNotifications() {
        return findPendingNotifications(0);
    }

    public List<PendingEventEmailNotification> findPendingNotifications(final int razTra) {
        final String sql = "EXEC " + storedProcedureName + " @p_nRazTra = ?";
        return jdbcTemplate.queryForList(sql, razTra)
                .stream()
                .map(this::mapRow)
                .toList();
    }

    private PendingEventEmailNotification mapRow(final Map<String, Object> row) {
        return new PendingEventEmailNotification(
                stringValue(row, "cEmail", "correo", "email", "recipientEmail"),
                stringValue(row, "Cliente", "cliente", "clientName", "razonSocial"),
                stringValue(row, "Placa", "placa", "plate", "unidad", "unitCode", "codUnidad"),
                stringValue(row, "Evento", "evento", "event", "eventDescription", "triggeringEventDescription"),
                stringValue(row, "nLat", "lat", "latitude"),
                stringValue(row, "nLon", "lon", "lng", "longitude"),
                stringValue(row, "Ubicacion", "ubicacion", "location")
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
}
