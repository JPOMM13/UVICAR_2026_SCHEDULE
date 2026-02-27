package com.jpomm.schedulerbase.reports.facade;

import java.util.List;
import java.util.Map;

public interface ClientesGrandesReportFacade {
    List<Map<String, Object>> listUnitsWithoutTransmission3Days(int codUsuario);
}
