package com.jpomm.schedulerbase.transmission.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TransmissionTemplateProvider {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private volatile List<TransmissionTemplateRow> cached;
    private volatile Map<Integer, List<TransmissionTemplateRow>> byCodUni;

    public TransmissionTemplateProvider(final ObjectMapper objectMapper,
                                        final ResourceLoader resourceLoader) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.resourceLoader = Objects.requireNonNull(resourceLoader);
    }

    public List<TransmissionTemplateRow> load(final String location) {
        if (cached != null) return cached;

        synchronized (this) {
            if (cached != null) return cached;

            final Resource resource = resourceLoader.getResource(location);
            try (InputStream in = resource.getInputStream()) {
                final List<Map<String, Object>> rows = objectMapper.readValue(in, new TypeReference<>() {});
                final List<TransmissionTemplateRow> parsed = new ArrayList<>(rows.size());

                for (Map<String, Object> r : rows) {
                    parsed.add(new TransmissionTemplateRow(
                            asString(r.get("PLACA")),
                            asInt(r.get("nCodUni")),
                            asDecimal(r.get("nLat")),
                            asDecimal(r.get("nLon")),
                            asInt(r.get("nVel")),
                            asInt(r.get("nRumbo")),
                            asDecimal(r.get("nAlt")),
                            asString(r.get("cRef")),
                            asString(r.get("cEstEnt")),
                            asString(r.get("cEstSal")),
                            asString(r.get("cDallas")),
                            asInt(r.get("nEnCerco")),
                            asInt(r.get("nNumSat")),
                            asInt(r.get("nDurEve")),
                            asDecimal(r.get("nDist")),
                            asDecimal(r.get("nComb")),
                            asDecimal(r.get("nCombReal")),
                            asDecimal(r.get("nBatPrin")),
                            asDecimal(r.get("nBatResp")),
                            asInt(r.get("nCodCabRut")),
                            asInt(r.get("nRazTra")),
                            asInt(r.get("nRazTraPro")),
                            asBool(r.get("nAlarLei")),
                            asInt(r.get("nAlarCodUsu")),
                            asString(r.get("cEntSal"))
                    ));
                }

                this.cached = Collections.unmodifiableList(parsed);
                this.byCodUni = parsed.stream()
                        .filter(x -> x.nCodUni() != null)
                        .collect(Collectors.groupingBy(TransmissionTemplateRow::nCodUni));

                return this.cached;
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot load templates from: " + location, ex);
            }
        }
    }

    public Map<Integer, List<TransmissionTemplateRow>> byCodUni(final String location) {
        load(location);
        return byCodUni;
    }

    private static String asString(final Object v) {
        return v == null ? null : String.valueOf(v);
    }

    private static Integer asInt(final Object v) {
        if (v == null) return null;
        final String s = String.valueOf(v).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        return Integer.valueOf(s);
    }

    private static Boolean asBool(final Object v) {
        if (v == null) return null;
        if (v instanceof Boolean b) return b;
        final String s = String.valueOf(v).trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty() || "null".equals(s)) return null;
        if ("1".equals(s) || "true".equals(s)) return true;
        if ("0".equals(s) || "false".equals(s)) return false;
        return null;
    }

    private static BigDecimal asDecimal(final Object v) {
        if (v == null) return null;
        final String s = String.valueOf(v).trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        return new BigDecimal(s);
    }
}
