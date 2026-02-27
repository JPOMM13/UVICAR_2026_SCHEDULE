package com.jpomm.schedulerbase.reports.service;

import java.util.List;
import java.util.Map;

public interface ClientesGrandesReportService {
    List<Map<String, Object>> listUnitsWithoutTransmission3Days(int codUsuario);
}
