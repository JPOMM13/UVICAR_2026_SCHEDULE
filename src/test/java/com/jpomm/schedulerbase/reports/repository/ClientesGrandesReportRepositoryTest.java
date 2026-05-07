package com.jpomm.schedulerbase.reports.repository;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientesGrandesReportRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void fetchUnitsWithoutTransmission3DaysExecutesConfiguredStoredProcedure() {
        final List<Map<String, Object>> expected = List.of();
        final ClientesGrandesReportRepository repository =
                new ClientesGrandesReportRepository(
                        jdbcTemplate,
                        "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                        "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                        "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                        "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
                );
        when(jdbcTemplate.queryForList(anyString())).thenReturn(expected);

        final List<Map<String, Object>> actual = repository.fetchUnitsWithoutTransmission3Days();

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture());
        assertThat(actual).isSameAs(expected);
        assertThat(sqlCaptor.getValue()).isEqualTo("EXEC dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes");
    }

    @Test
    void fetchMtcOsinergUnitsWithoutTransmission15MinutesTo2HoursExecutesConfiguredStoredProcedure() {
        final List<Map<String, Object>> expected = List.of();
        final ClientesGrandesReportRepository repository =
                new ClientesGrandesReportRepository(
                        jdbcTemplate,
                        "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                        "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                        "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                        "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
                );
        when(jdbcTemplate.queryForList(anyString())).thenReturn(expected);

        final List<Map<String, Object>> actual =
                repository.fetchMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours();

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture());
        assertThat(actual).isSameAs(expected);
        assertThat(sqlCaptor.getValue()).isEqualTo("EXEC dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs");
    }

    @Test
    void fetchMtcOsinergUnitsWithoutTransmission2HoursTo2DaysExecutesConfiguredStoredProcedure() {
        final List<Map<String, Object>> expected = List.of();
        final ClientesGrandesReportRepository repository =
                new ClientesGrandesReportRepository(
                        jdbcTemplate,
                        "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                        "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                        "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                        "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
                );
        when(jdbcTemplate.queryForList(anyString())).thenReturn(expected);

        final List<Map<String, Object>> actual =
                repository.fetchMtcOsinergUnitsWithoutTransmission2HoursTo2Days();

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture());
        assertThat(actual).isSameAs(expected);
        assertThat(sqlCaptor.getValue()).isEqualTo("EXEC dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias");
    }

    @Test
    void fetchMtcOsinergUnitsWithoutTransmissionMoreThan2DaysExecutesConfiguredStoredProcedure() {
        final List<Map<String, Object>> expected = List.of();
        final ClientesGrandesReportRepository repository =
                new ClientesGrandesReportRepository(
                        jdbcTemplate,
                        "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                        "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                        "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                        "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
                );
        when(jdbcTemplate.queryForList(anyString())).thenReturn(expected);

        final List<Map<String, Object>> actual =
                repository.fetchMtcOsinergUnitsWithoutTransmissionMoreThan2Days();

        final ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForList(sqlCaptor.capture());
        assertThat(actual).isSameAs(expected);
        assertThat(sqlCaptor.getValue()).isEqualTo("EXEC dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias");
    }

    @Test
    void constructorRejectsUnsafeStoredProcedureName() {
        assertThatThrownBy(() -> new ClientesGrandesReportRepository(
                jdbcTemplate,
                "dbo.proc; DROP PROC x",
                "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid stored procedure name for uvicar.reports.clientes-grandes.sin-trans-3dias.sp");
    }

    @Test
    void constructorRejectsUnsafeMtcOsinergStoredProcedureName() {
        assertThatThrownBy(() -> new ClientesGrandesReportRepository(
                jdbcTemplate,
                "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                "dbo.proc; DROP PROC x",
                "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid stored procedure name for uvicar.reports.unidades.sin-transm-mtcosinrg-15min-a-2hrs.sp");
    }

    @Test
    void constructorRejectsUnsafeMtcOsinerg2HoursTo2DaysStoredProcedureName() {
        assertThatThrownBy(() -> new ClientesGrandesReportRepository(
                jdbcTemplate,
                "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                "dbo.proc; DROP PROC x",
                "dbo.pa_uniSinTransm_MTCOSINRG_MasDe2Dias"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid stored procedure name for uvicar.reports.unidades.sin-transm-mtcosinrg-2hrs-a-2dias.sp");
    }

    @Test
    void constructorRejectsUnsafeMtcOsinergMoreThan2DaysStoredProcedureName() {
        assertThatThrownBy(() -> new ClientesGrandesReportRepository(
                jdbcTemplate,
                "dbo.pa_RptUniActSinTrans3Dias_ClientesGrandes",
                "dbo.pa_uniSinTransm_MTCOSINRG_15MinA2Hrs",
                "dbo.pa_uniSinTransm_MTCOSINRG_2HrsA2Dias",
                "dbo.proc; DROP PROC x"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid stored procedure name for uvicar.reports.unidades.sin-transm-mtcosinrg-mas-de-2dias.sp");
    }
}
