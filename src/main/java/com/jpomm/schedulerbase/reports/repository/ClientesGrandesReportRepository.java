package com.jpomm.schedulerbase.reports.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class ClientesGrandesReportRepository {

    private static final Pattern SAFE_PROC = Pattern.compile("^[A-Za-z0-9_\\.\\[\\]]+$");

    private final JdbcTemplate jdbcTemplate;
    private final String unitsWithoutTransmission3DaysStoredProcedureName;
    private final String mtcOsinergUnitsWithoutTransmission15MinutesTo2HoursStoredProcedureName;
    private final String mtcOsinergUnitsWithoutTransmission2HoursTo2DaysStoredProcedureName;
    private final String mtcOsinergUnitsWithoutTransmissionMoreThan2DaysStoredProcedureName;

    public ClientesGrandesReportRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.reports.clientes-grandes.sin-trans-3dias.sp:pa_RptUniActSinTrans3Dias_ClientesGrandes}")
            final String unitsWithoutTransmission3DaysStoredProcedureName,
            @Value("${uvicar.reports.unidades.sin-transm-mtcosinrg-15min-a-2hrs.sp:pa_uniSinTransm_MTCOSINRG_15MinA2Hrs}")
            final String mtcOsinergUnitsWithoutTransmission15MinutesTo2HoursStoredProcedureName,
            @Value("${uvicar.reports.unidades.sin-transm-mtcosinrg-2hrs-a-2dias.sp:pa_uniSinTransm_MTCOSINRG_2HrsA2Dias}")
            final String mtcOsinergUnitsWithoutTransmission2HoursTo2DaysStoredProcedureName,
            @Value("${uvicar.reports.unidades.sin-transm-mtcosinrg-mas-de-2dias.sp:pa_uniSinTransm_MTCOSINRG_MasDe2Dias}")
            final String mtcOsinergUnitsWithoutTransmissionMoreThan2DaysStoredProcedureName
    ) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.unitsWithoutTransmission3DaysStoredProcedureName = validateStoredProcedureName(
                unitsWithoutTransmission3DaysStoredProcedureName,
                "uvicar.reports.clientes-grandes.sin-trans-3dias.sp"
        );
        this.mtcOsinergUnitsWithoutTransmission15MinutesTo2HoursStoredProcedureName = validateStoredProcedureName(
                mtcOsinergUnitsWithoutTransmission15MinutesTo2HoursStoredProcedureName,
                "uvicar.reports.unidades.sin-transm-mtcosinrg-15min-a-2hrs.sp"
        );
        this.mtcOsinergUnitsWithoutTransmission2HoursTo2DaysStoredProcedureName = validateStoredProcedureName(
                mtcOsinergUnitsWithoutTransmission2HoursTo2DaysStoredProcedureName,
                "uvicar.reports.unidades.sin-transm-mtcosinrg-2hrs-a-2dias.sp"
        );
        this.mtcOsinergUnitsWithoutTransmissionMoreThan2DaysStoredProcedureName = validateStoredProcedureName(
                mtcOsinergUnitsWithoutTransmissionMoreThan2DaysStoredProcedureName,
                "uvicar.reports.unidades.sin-transm-mtcosinrg-mas-de-2dias.sp"
        );
    }

    public List<Map<String, Object>> fetchUnitsWithoutTransmission3Days() {
        final String sql = "EXEC " + unitsWithoutTransmission3DaysStoredProcedureName;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> fetchMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours() {
        final String sql = "EXEC " + mtcOsinergUnitsWithoutTransmission15MinutesTo2HoursStoredProcedureName;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> fetchMtcOsinergUnitsWithoutTransmission2HoursTo2Days() {
        final String sql = "EXEC " + mtcOsinergUnitsWithoutTransmission2HoursTo2DaysStoredProcedureName;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> fetchMtcOsinergUnitsWithoutTransmissionMoreThan2Days() {
        final String sql = "EXEC " + mtcOsinergUnitsWithoutTransmissionMoreThan2DaysStoredProcedureName;
        return jdbcTemplate.queryForList(sql);
    }

    private static String validateStoredProcedureName(final String name, final String propertyName) {
        final String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty() || !SAFE_PROC.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid stored procedure name for " + propertyName);
        }
        return trimmed;
    }
}
