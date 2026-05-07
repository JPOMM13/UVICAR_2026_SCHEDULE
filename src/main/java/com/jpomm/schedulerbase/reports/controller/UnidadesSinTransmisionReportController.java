package com.jpomm.schedulerbase.reports.controller;

import com.jpomm.schedulerbase.reports.facade.ClientesGrandesReportFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/reports/unidades")
public class UnidadesSinTransmisionReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnidadesSinTransmisionReportController.class);

    private final ClientesGrandesReportFacade reportFacade;

    public UnidadesSinTransmisionReportController(final ClientesGrandesReportFacade reportFacade) {
        this.reportFacade = Objects.requireNonNull(reportFacade);
    }

    /**
     * GET /api/reports/unidades/sin-transm-mtcosinrg-15min-a-2hrs
     * Ejecuta el SP pa_uniSinTransm_MTCOSINRG_15MinA2Hrs y retorna su resultado.
     */
    @GetMapping("/sin-transm-mtcosinrg-15min-a-2hrs")
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours(
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(),
                request.getRequestURI(), request.getRemoteAddr());
        return reportFacade.listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours();
    }

    /**
     * GET /api/reports/unidades/sin-transm-mtcosinrg-2hrs-a-2dias
     * Ejecuta el SP pa_uniSinTransm_MTCOSINRG_2HrsA2Dias y retorna su resultado.
     */
    @GetMapping("/sin-transm-mtcosinrg-2hrs-a-2dias")
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission2HoursTo2Days(
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(),
                request.getRequestURI(), request.getRemoteAddr());
        return reportFacade.listMtcOsinergUnitsWithoutTransmission2HoursTo2Days();
    }

    /**
     * GET /api/reports/unidades/sin-transm-mtcosinrg-mas-de-2dias
     * Ejecuta el SP pa_uniSinTransm_MTCOSINRG_MasDe2Dias y retorna su resultado.
     */
    @GetMapping("/sin-transm-mtcosinrg-mas-de-2dias")
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmissionMoreThan2Days(
            final HttpServletRequest request
    ) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(),
                request.getRequestURI(), request.getRemoteAddr());
        return reportFacade.listMtcOsinergUnitsWithoutTransmissionMoreThan2Days();
    }
}
