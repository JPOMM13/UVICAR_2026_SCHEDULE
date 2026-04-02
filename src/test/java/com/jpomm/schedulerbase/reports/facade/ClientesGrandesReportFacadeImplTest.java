package com.jpomm.schedulerbase.reports.facade;

import com.jpomm.schedulerbase.reports.service.ClientesGrandesReportService;
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
class ClientesGrandesReportFacadeImplTest {

    @Mock
    private ClientesGrandesReportService service;

    @InjectMocks
    private ClientesGrandesReportFacadeImpl facade;

    @Test
    void listUnitsWithoutTransmission3DaysDelegatesToService() {
        final List<Map<String, Object>> expected = List.of();
        when(service.listUnitsWithoutTransmission3Days()).thenReturn(expected);

        final List<Map<String, Object>> actual = facade.listUnitsWithoutTransmission3Days();

        assertThat(actual).isSameAs(expected);
        verify(service).listUnitsWithoutTransmission3Days();
    }
}
