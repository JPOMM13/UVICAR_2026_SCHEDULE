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
    private final String storedProcedureName;

    public ClientesGrandesReportRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.reports.clientes-grandes.sin-trans-3dias.sp:pa_RptUniActSinTrans3Dias_ClientesGrandes}")
            final String storedProcedureName
    ) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.storedProcedureName = validateStoredProcedureName(storedProcedureName);
    }

    public List<Map<String, Object>> fetchUnitsWithoutTransmission3Days(final int codUsuario) {
        if (codUsuario <= 0) {
            throw new IllegalArgumentException("codUsuario must be greater than 0");
        }
        final String sql = "EXEC " + storedProcedureName + " @p_codUsuario = ?";
        return jdbcTemplate.queryForList(sql, codUsuario);
    }

    private static String validateStoredProcedureName(final String name) {
        final String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty() || !SAFE_PROC.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid stored procedure name for uvicar.reports.clientes-grandes.sin-trans-3dias.sp");
        }
        return trimmed;
    }
}
