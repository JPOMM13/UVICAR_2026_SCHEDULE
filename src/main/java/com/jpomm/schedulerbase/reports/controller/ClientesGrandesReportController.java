package com.jpomm.schedulerbase.reports.controller;

import com.jpomm.schedulerbase.reports.facade.ClientesGrandesReportFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/reports/clientes-grandes")
public class ClientesGrandesReportController {

    private final ClientesGrandesReportFacade reportFacade;

    public ClientesGrandesReportController(final ClientesGrandesReportFacade reportFacade) {
        this.reportFacade = Objects.requireNonNull(reportFacade);
    }

    /**
     * GET /api/reports/clientes-grandes/sin-trans-3dias
     * Ejecuta el SP pa_RptUniActSinTrans3Dias_ClientesGrandes y retorna su resultado.
     */
    @GetMapping("/sin-trans-3dias")
    public List<Map<String, Object>> listUnitsWithoutTransmission3Days(
            @RequestParam("codUsuario") final int codUsuario
    ) {
        return reportFacade.listUnitsWithoutTransmission3Days(codUsuario);
    }
}
