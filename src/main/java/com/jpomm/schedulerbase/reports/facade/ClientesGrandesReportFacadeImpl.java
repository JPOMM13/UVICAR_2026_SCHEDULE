package com.jpomm.schedulerbase.reports.facade;

import com.jpomm.schedulerbase.reports.service.ClientesGrandesReportService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ClientesGrandesReportFacadeImpl implements ClientesGrandesReportFacade {

    private final ClientesGrandesReportService reportService;

    public ClientesGrandesReportFacadeImpl(final ClientesGrandesReportService reportService) {
        this.reportService = Objects.requireNonNull(reportService);
    }

    @Override
    public List<Map<String, Object>> listUnitsWithoutTransmission3Days(final int codUsuario) {
        return reportService.listUnitsWithoutTransmission3Days(codUsuario);
    }
}
