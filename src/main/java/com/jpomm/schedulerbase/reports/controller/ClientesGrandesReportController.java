package com.jpomm.schedulerbase.reports.controller;

import com.jpomm.schedulerbase.reports.facade.ClientesGrandesReportFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientesGrandesReportController.class);

    private final ClientesGrandesReportFacade reportFacade;

    public ClientesGrandesReportController(final ClientesGrandesReportFacade reportFacade) {
        this.reportFacade = Objects.requireNonNull(reportFacade);
    }

    /**
     * GET /api/reports/clientes-grandes/sin-trans-3dias
     * Ejecuta el SP pa_RptUniActSinTrans3Dias_ClientesGrandes y retorna su resultado.
     * El query param codUsuario se mantiene como opcional por compatibilidad,
     * pero el procedimiento actual no recibe parámetros.
     */
    @GetMapping("/sin-trans-3dias")
    public List<Map<String, Object>> listUnitsWithoutTransmission3Days(
            @RequestParam(value = "codUsuario", required = false) @SuppressWarnings("unused") final Integer codUsuario,
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] codUsuario={} desde {}", request.getMethod(),
                request.getRequestURI(), codUsuario, request.getRemoteAddr());
        return reportFacade.listUnitsWithoutTransmission3Days();
    }
}
