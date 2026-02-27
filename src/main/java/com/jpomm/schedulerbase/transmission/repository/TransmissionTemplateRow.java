package com.jpomm.schedulerbase.transmission.repository;

import java.math.BigDecimal;

public record TransmissionTemplateRow(
        String placa,
        Integer nCodUni,
        BigDecimal nLat,
        BigDecimal nLon,
        Integer nVel,
        Integer nRumbo,
        BigDecimal nAlt,
        String cRef,
        String cEstEnt,
        String cEstSal,
        String cDallas,
        Integer nEnCerco,
        Integer nNumSat,
        Integer nDurEve,
        BigDecimal nDist,
        BigDecimal nComb,
        BigDecimal nCombReal,
        BigDecimal nBatPrin,
        BigDecimal nBatResp,
        Integer nCodCabRut,
        Integer nRazTra,
        Integer nRazTraPro,
        Boolean nAlarLei,
        Integer nAlarCodUsu,
        String cEntSal
) {
}
