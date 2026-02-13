package com.jpomm.schedulerbase.transmission.repository;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Repository
public class TransmissionQueryRepository {

    private static final Pattern SAFE_TABLE = Pattern.compile("^[A-Za-z0-9_\\.\\[\\]]+$");

    private final JdbcTemplate jdbcTemplate;
    private final String table;
    private final int lastMinutes;
    private final int top;

    public TransmissionQueryRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.transmissions.table:tTransmisiones}") final String table,
            @Value("${uvicar.transmissions.last-minutes:10}") final int lastMinutes,
            @Value("${uvicar.transmissions.top:200}") final int top
    ) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
        this.table = validateTable(table);
        this.lastMinutes = lastMinutes;
        this.top = top;
    }

    /**
     * Consulta SIN parámetros.
     * - Tabla configurable por propiedad: uvicar.transmissions.table (default: tTransmisiones)
     * - Ventana configurable: uvicar.transmissions.last-minutes (default: 10)
     * - Máximo configurable: uvicar.transmissions.top (default: 200)
     *
     * La ventana se calcula en SQL Server usando la hora actual: SYSDATETIME()
     */
    public List<TransmissionResponse> listLatestTransmissions() {

        // TOP (CAST(? AS INT)) permite parametrizar TOP en SQL Server
        final String sql = ("""
                SELECT TOP (CAST(? AS INT))
                       dFecCom, dFecMen, dFecPro,
                       nLat, nLon, nVel, nRumbo, nAlt,
                       cRef, nCodUni, cEstEnt, cEstSal, cDalias
                FROM %s WITH (NOLOCK)
                WHERE dFecCom >= DATEADD(MINUTE, -?, SYSDATETIME())
                ORDER BY dFecCom DESC
                """).formatted(table);

        return jdbcTemplate.query(sql, TransmissionRowMapper.INSTANCE, top, lastMinutes);
    }

    private static String validateTable(final String table) {
        final String trimmed = table == null ? "" : table.trim();
        if (trimmed.isEmpty() || !SAFE_TABLE.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid table name for uvicar.transmissions.table");
        }
        return trimmed;
    }

    private static final class TransmissionRowMapper implements RowMapper<TransmissionResponse> {
        private static final TransmissionRowMapper INSTANCE = new TransmissionRowMapper();

        @Override
        public TransmissionResponse mapRow(final ResultSet rs, final int rowNum) throws SQLException {
            return new TransmissionResponse(
                    rs.getTimestamp("dFecCom") != null ? rs.getTimestamp("dFecCom").toLocalDateTime() : null,
                    rs.getTimestamp("dFecMen") != null ? rs.getTimestamp("dFecMen").toLocalDateTime() : null,
                    rs.getTimestamp("dFecPro") != null ? rs.getTimestamp("dFecPro").toLocalDateTime() : null,
                    rs.getBigDecimal("nLat"),
                    rs.getBigDecimal("nLon"),
                    rs.getObject("nVel") != null ? rs.getInt("nVel") : null,
                    rs.getObject("nRumbo") != null ? rs.getInt("nRumbo") : null,
                    rs.getBigDecimal("nAlt") != null ? rs.getBigDecimal("nAlt").intValue() : null,
                    rs.getString("cRef"),
                    rs.getObject("nCodUni") != null ? rs.getInt("nCodUni") : null,
                    rs.getString("cEstEnt"),
                    rs.getString("cEstSal"),
                    rs.getString("cDalias")
            );
        }
    }
}
