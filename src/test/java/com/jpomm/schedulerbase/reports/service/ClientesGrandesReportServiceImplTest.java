package com.jpomm.schedulerbase.reports.service;

import com.jpomm.schedulerbase.reports.repository.ClientesGrandesReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientesGrandesReportServiceImplTest {

    @Mock
    private ClientesGrandesReportRepository repository;

    @InjectMocks
    private ClientesGrandesReportServiceImpl service;

    @Test
    void listUnitsWithoutTransmission3DaysDelegatesToRepository() {
        final List<Map<String, Object>> expected = List.of();
        when(repository.fetchUnitsWithoutTransmission3Days()).thenReturn(expected);

        final List<Map<String, Object>> actual = service.listUnitsWithoutTransmission3Days();

        assertThat(actual).isSameAs(expected);
        verify(repository).fetchUnitsWithoutTransmission3Days();
    }
}
