package com.jpomm.schedulerbase.reports.service;

import com.jpomm.schedulerbase.reports.repository.ClientesGrandesReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ClientesGrandesReportServiceImpl implements ClientesGrandesReportService {

    private final ClientesGrandesReportRepository reportRepository;

    public ClientesGrandesReportServiceImpl(final ClientesGrandesReportRepository reportRepository) {
        this.reportRepository = Objects.requireNonNull(reportRepository);
    }

    @Override
    public List<Map<String, Object>> listUnitsWithoutTransmission3Days() {
        return reportRepository.fetchUnitsWithoutTransmission3Days();
    }

    @Override
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours() {
        return reportRepository.fetchMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours();
    }

    @Override
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmission2HoursTo2Days() {
        return reportRepository.fetchMtcOsinergUnitsWithoutTransmission2HoursTo2Days();
    }

    @Override
    public List<Map<String, Object>> listMtcOsinergUnitsWithoutTransmissionMoreThan2Days() {
        return reportRepository.fetchMtcOsinergUnitsWithoutTransmissionMoreThan2Days();
    }
}
