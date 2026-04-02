package com.jpomm.schedulerbase.transmission.repository;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransmissionQueryRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void listLatestTransmissionsBuildsExpectedSqlAndDelegatesToJdbcTemplate() {
        final List<TransmissionResponse> expected = List.of();
        final TransmissionQueryRepository repository = new TransmissionQueryRepository(jdbcTemplate, "dbo.tTransmisiones", 15, 50);
        doReturn(expected).when(jdbcTemplate).query(anyString(), any(RowMapper.class), anyInt(), anyInt());

        final List<TransmissionResponse> actual = repository.listLatestTransmissions();

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(RowMapper.class), anyInt(), anyInt());
        assertThat(actual).isSameAs(expected);
        assertThat(sqlCaptor.getValue()).contains("FROM dbo.tTransmisiones WITH (NOLOCK)");
        assertThat(sqlCaptor.getValue()).contains("DATEADD(MINUTE, -?, SYSDATETIME())");
        assertThat(sqlCaptor.getValue()).contains("TOP (CAST(? AS INT))");
    }

    @Test
    void constructorRejectsUnsafeTableName() {
        assertThatThrownBy(() -> new TransmissionQueryRepository(jdbcTemplate, "dbo.tTransmisiones; DROP TABLE x", 10, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid table name for uvicar.transmissions.table");
    }
}
