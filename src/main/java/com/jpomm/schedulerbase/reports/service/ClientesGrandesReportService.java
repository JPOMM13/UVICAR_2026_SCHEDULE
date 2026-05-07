package com.jpomm.schedulerbase.reports.service;

import java.util.List;
import java.util.Map;

public interface ClientesGrandesReportService {
    List<Map<String, Object>> listUnitsWithoutTransmission3Days();

    List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours();

    List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission2HoursTo2Days();

    List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmissionMoreThan2Days();
}
