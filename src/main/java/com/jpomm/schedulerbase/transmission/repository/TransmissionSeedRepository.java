package com.jpomm.schedulerbase.transmission.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;

@Repository
public class TransmissionSeedRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final String tableName;
    private final boolean disableTriggersDuringSeed;
    private final Random random = new Random();

    private volatile String insertSql;
    private volatile List<String> columnsUsed;
    private volatile Map<String, ColumnMeta> columnMeta;

    public TransmissionSeedRepository(
            final JdbcTemplate jdbcTemplate,
            @Value("${uvicar.transmissions.table:tTransmisiones}") final String tableName,
            @Value("${jobs.seed.disable-triggers:true}") final boolean disableTriggersDuringSeed
    ) {
        this.jdbc = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate));
        this.tableName = Objects.requireNonNull(tableName);
        this.disableTriggersDuringSeed = disableTriggersDuringSeed;
    }

    public int insertRandomRows(final int rows) {
        if (rows <= 0) return 0;

        ensureInsertSqlInitialized();

        if (disableTriggersDuringSeed) {
            // OJO: requiere permisos. Si no los tienes, pon jobs.seed.disable-triggers=false
            jdbc.getJdbcTemplate().execute("DISABLE TRIGGER ALL ON dbo." + tableName);
        }

        try {
            int inserted = 0;
            for (int i = 0; i < rows; i++) {
                final MapSqlParameterSource filtered = buildRowParamsFiltered();
                try {
                    inserted += jdbc.update(insertSql, filtered);
                } catch (Exception ex) {
                    // log detallado para encontrar el valor exacto que provoca overflow
                    throw new IllegalStateException("Seed insert failed. Params=" + debugParams(filtered), ex);
                }
            }
            return inserted;
        } finally {
            if (disableTriggersDuringSeed) {
                jdbc.getJdbcTemplate().execute("ENABLE TRIGGER ALL ON dbo." + tableName);
            }
        }
    }

    private MapSqlParameterSource buildRowParamsFiltered() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime fecCom = now.minusSeconds(random.nextInt(60 * 60));
        final LocalDateTime fecMen = fecCom.plusSeconds(random.nextInt(10));
        final LocalDateTime fecPro = fecMen.plusSeconds(random.nextInt(10));

        final int vel = random.nextInt(121);
        final int rumbo = random.nextInt(360);
        final int enCerco = random.nextInt(2);

        // smallint => 1000..1199 OK
        final int codUni = 1000 + random.nextInt(200);
        final String ref = "U" + codUni;

        final boolean alarLei = random.nextInt(50) == 0;
        final LocalDateTime alarFh = alarLei ? fecMen : null;
        final Integer alarCodUsu = alarLei ? (1 + random.nextInt(9999)) : null; // int => OK

        // Con tu schema real (11,8) esto entra perfecto
        final BigDecimal lat = randomDecimal(-18.0, -16.0, 8);
        final BigDecimal lon = randomDecimal(-72.5, -70.0, 8);

        // decimal(10,2) => sobra
        final BigDecimal dist = randomDecimal(0, 200, 2);

        // decimal(5,2) => max 999.99 => sobra
        final BigDecimal comb = randomDecimal(0, 100, 2);
        final BigDecimal combReal = comb.add(randomDecimal(-5, 5, 2));

        // smallint => OK con 70/34/302
        final int razTra = pickRazTraCandidate();
        final int razTraPro = pickRazTraCandidate();

        // tinyint => 0..255
        final int numSat = 5 + random.nextInt(16);

        final String estEnt = "ENT";
        final String estSal = "SAL";
        final String entSal = "ENT:" + estEnt + "|SAL:" + estSal;

        final int durEve = random.nextInt(600); // int
        // decimal(7,2) => max 99999.99 => sobra
        final BigDecimal alt = randomDecimal(0, 4500, 2);

        // decimal(4,2) => max 99.99 => sobrado
        final BigDecimal batPrin = randomDecimal(11.5, 13.2, 2);
        final BigDecimal batResp = randomDecimal(3.5, 4.2, 2);

        // smallint => OK
        final int codCabRut = 1 + random.nextInt(30);

        final MapSqlParameterSource p = new MapSqlParameterSource();

        p.addValue("dFecCom", Timestamp.valueOf(fecCom));
        p.addValue("dFecMen", Timestamp.valueOf(fecMen));
        p.addValue("dFecPro", Timestamp.valueOf(fecPro));

        addDecimalClamped(p, "nLat", lat);
        addDecimalClamped(p, "nLon", lon);

        addIntClamped(p, "nRumbo", rumbo);
        addIntClamped(p, "nVel", vel);
        addIntClamped(p, "nEnCerco", enCerco);

        p.addValue("cRef", ref);

        // bit
        p.addValue("nAlarLei", alarLei);
        p.addValue("dAlarLeiFecHor", alarFh != null ? Timestamp.valueOf(alarFh) : null);

        if (alarCodUsu != null) addIntClamped(p, "nAlarCodUsu", alarCodUsu);
        else p.addValue("nAlarCodUsu", null);

        addDecimalClamped(p, "nDist", dist);
        addDecimalClamped(p, "nComb", comb);
        addDecimalClamped(p, "nCombReal", combReal);

        addIntClamped(p, "nCodUni", codUni);
        addIntClamped(p, "nRazTra", razTra);
        addIntClamped(p, "nRazTraPro", razTraPro);
        addIntClamped(p, "nNumSat", numSat);

        p.addValue("cEstEnt", estEnt);
        p.addValue("cEstSal", estSal);

        addIntClamped(p, "nDurEve", durEve);
        addDecimalClamped(p, "nAlt", alt);

        addDecimalClamped(p, "nBatPrin", batPrin);
        addDecimalClamped(p, "nBatResp", batResp);

        p.addValue("cEntSal", entSal);

        addIntClamped(p, "nCodCabRut", codCabRut);

        // opcional: tu caso VARCHAR(1)
        if (columnsUsed.contains("cFlagQuellaveco")) {
            p.addValue("cFlagQuellaveco", random.nextBoolean() ? "1" : "0");
        }

        // Filtrar SOLO columnas del INSERT (orden/consistencia)
        final MapSqlParameterSource filtered = new MapSqlParameterSource();
        for (String col : columnsUsed) {
            filtered.addValue(col, p.hasValue(col) ? p.getValue(col) : null);
        }
        return filtered;
    }

    private String debugParams(final MapSqlParameterSource p) {
        final StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (String col : columnsUsed) {
            sj.add(col + "=" + p.getValue(col));
        }
        return sj.toString();
    }

    private BigDecimal randomDecimal(final double min, final double max, final int scale) {
        final double v = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP);
    }

    private int pickRazTraCandidate() {
        final int r = random.nextInt(100);
        if (r < 50) return 70;
        if (r < 80) return 34;
        if (r < 95) return 302;
        return 1 + random.nextInt(10);
    }

    private void ensureInsertSqlInitialized() {
        if (this.insertSql != null) return;

        synchronized (this) {
            if (this.insertSql != null) return;

            this.columnMeta = loadColumnMeta();

            final List<String> dbColumns = jdbc.getJdbcTemplate().query(
                    "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dbo' AND TABLE_NAME=?",
                    (rs, rowNum) -> rs.getString(1),
                    this.tableName
            );

            final Set<String> db = new HashSet<>();
            for (String c : dbColumns) {
                if (c != null) db.add(c.trim());
            }

            final List<String> known = List.of(
                    "dFecCom", "dFecMen", "dFecPro",
                    "nLat", "nLon",
                    "nRumbo", "nVel", "nEnCerco",
                    "cRef",
                    "nAlarLei", "dAlarLeiFecHor", "nAlarCodUsu",
                    "nDist", "nComb", "nCodUni",
                    "nRazTra", "nNumSat",
                    "cEstEnt", "cEstSal",
                    "cFlagQuellaveco",
                    "nDurEve", "nAlt", "nCombReal",
                    "nBatPrin", "nBatResp",
                    "cEntSal",
                    "nCodCabRut", "nRazTraPro"
            );

            final List<String> cols = new ArrayList<>();
            for (String k : known) {
                if (db.contains(k)) cols.add(k);
            }

            if (cols.isEmpty()) {
                throw new IllegalStateException("No columns found for insert in dbo." + tableName);
            }

            final String columnsPart = String.join(", ", cols);
            final String valuesPart = ":" + String.join(", :", cols);

            this.insertSql = "INSERT INTO dbo." + tableName + " (" + columnsPart + ") VALUES (" + valuesPart + ")";
            this.columnsUsed = cols;
        }
    }

    private Map<String, ColumnMeta> loadColumnMeta() {
        final String sql =
                "SELECT COLUMN_NAME, DATA_TYPE, " +
                        "COALESCE(NUMERIC_PRECISION,0) AS NUMERIC_PRECISION, " +
                        "COALESCE(NUMERIC_SCALE,0) AS NUMERIC_SCALE " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA='dbo' AND TABLE_NAME=?";

        final List<ColumnMeta> metas = jdbc.getJdbcTemplate().query(
                sql,
                (rs, rowNum) -> new ColumnMeta(
                        rs.getString("COLUMN_NAME"),
                        rs.getString("DATA_TYPE"),
                        rs.getInt("NUMERIC_PRECISION"),
                        rs.getInt("NUMERIC_SCALE")
                ),
                this.tableName
        );

        final Map<String, ColumnMeta> map = new HashMap<>();
        for (ColumnMeta m : metas) {
            map.put(m.name.toLowerCase(Locale.ROOT), m);
        }
        return map;
    }

    private void addIntClamped(final MapSqlParameterSource p, final String column, final int value) {
        // Con tu schema actual (smallint/tinyint/int) nunca debería overflow con nuestros rangos,
        // pero lo dejamos por compatibilidad con otros ambientes.
        final ColumnMeta meta = columnMeta.get(column.toLowerCase(Locale.ROOT));
        if (meta == null) {
            p.addValue(column, value);
            return;
        }

        final String t = meta.dataType;
        long v = value;

        if ("tinyint".equals(t)) {
            if (v < 0) v = 0;
            if (v > 255) v = 255;
            p.addValue(column, v);
            return;
        }
        if ("smallint".equals(t)) {
            if (v < Short.MIN_VALUE) v = Short.MIN_VALUE;
            if (v > Short.MAX_VALUE) v = Short.MAX_VALUE;
            p.addValue(column, (short) v);
            return;
        }
        if ("int".equals(t)) {
            p.addValue(column, value);
            return;
        }
        if ("bit".equals(t)) {
            p.addValue(column, value != 0);
            return;
        }

        p.addValue(column, value);
    }

    private void addDecimalClamped(final MapSqlParameterSource p, final String column, final BigDecimal value) {
        if (value == null) {
            p.addValue(column, null);
            return;
        }

        final ColumnMeta meta = columnMeta.get(column.toLowerCase(Locale.ROOT));
        if (meta == null || (!"decimal".equals(meta.dataType) && !"numeric".equals(meta.dataType))) {
            p.addValue(column, value);
            return;
        }

        // Ajustar escala exacta de la columna (ej: nLat scale 8)
        BigDecimal v = value.setScale(meta.scale, RoundingMode.HALF_UP);

        // Clamp por precisión: max = 10^(p-s) - 10^-s
        final int intDigits = Math.max(meta.precision - meta.scale, 1);
        BigDecimal maxIntPart = BigDecimal.TEN.pow(intDigits).subtract(BigDecimal.ONE);
        BigDecimal max = maxIntPart;
        if (meta.scale > 0) {
            BigDecimal frac = BigDecimal.ONE.subtract(BigDecimal.ONE.scaleByPowerOfTen(-meta.scale));
            max = maxIntPart.add(frac).setScale(meta.scale, RoundingMode.HALF_UP);
        } else {
            max = maxIntPart.setScale(0, RoundingMode.HALF_UP);
        }
        BigDecimal min = max.negate();

        if (v.compareTo(max) > 0) v = max;
        if (v.compareTo(min) < 0) v = min;

        p.addValue(column, v);
    }

    private static final class ColumnMeta {
        private final String name;
        private final String dataType;
        private final int precision;
        private final int scale;

        private ColumnMeta(final String name, final String dataType, final int precision, final int scale) {
            this.name = name;
            this.dataType = dataType != null ? dataType.trim().toLowerCase(Locale.ROOT) : "";
            this.precision = precision;
            this.scale = scale;
        }
    }
}