package com.jpomm.schedulerbase.notifications.repository;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventEmailNotificationRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void findPendingNotificationsExecutesConfiguredStoredProcedureAndMapsStoreColumns() {
        final EventEmailNotificationRepository repository =
                new EventEmailNotificationRepository(jdbcTemplate, "pa_envioCorreosEventosAlertas_1Hora");
        when(jdbcTemplate.queryForList(anyString(), eq(44))).thenReturn(List.of(Map.of(
                "Cliente", "AGROINDUSTRIAS SAN ANDRES S.A.C.",
                "Placa", "424722",
                "Evento", "Energia Principal desconectada",
                "nLat", "-12.58427660",
                "nLon", "-76.66905830",
                "Ubicacion", "LIMA / CANETE / SANTA CRUZ DE FLORES -",
                "cEmail", "cliente@correo.com"
        )));

        final List<PendingEventEmailNotification> notifications = repository.findPendingNotifications(44);

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture(), eq(44));
        assertThat(sqlCaptor.getValue()).isEqualTo("EXEC pa_envioCorreosEventosAlertas_1Hora @p_nRazTra = ?");
        assertThat(notifications).hasSize(1);

        final PendingEventEmailNotification notification = notifications.get(0);
        assertThat(notification.recipientEmail()).isEqualTo("cliente@correo.com");
        assertThat(notification.clientName()).isEqualTo("AGROINDUSTRIAS SAN ANDRES S.A.C.");
        assertThat(notification.plate()).isEqualTo("424722");
        assertThat(notification.event()).isEqualTo("Energia Principal desconectada");
        assertThat(notification.latitude()).isEqualTo("-12.58427660");
        assertThat(notification.longitude()).isEqualTo("-76.66905830");
        assertThat(notification.location()).isEqualTo("LIMA / CANETE / SANTA CRUZ DE FLORES -");
    }

    @Test
    void constructorRejectsUnsafeStoredProcedureName() {
        assertThatThrownBy(() -> new EventEmailNotificationRepository(
                jdbcTemplate,
                "pa_envioCorreosEventosAlertas_1Hora; DROP TABLE x"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid stored procedure name for uvicar.notifications.event-email.sp");
    }
}
