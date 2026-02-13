package com.jpomm.schedulerbase.transmission.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO liviano para consultas (read-only) sobre tTransmisiones.
 *
 * Importante: este microservicio asume que tTransmisiones ya existe.
 */
public record TransmissionResponse(
        LocalDateTime dFecCom,
        LocalDateTime dFecMen,
        LocalDateTime dFecPro,
        BigDecimal nLat,
        BigDecimal nLon,
        Integer nVel,
        Integer nRumbo,
        Integer nAlt,
        String cRef,
        Integer nCodUni,
        String cEstEnt,
        String cEstSal,
        String cDalias
) {
}
