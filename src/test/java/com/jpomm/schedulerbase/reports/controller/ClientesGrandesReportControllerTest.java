package com.jpomm.schedulerbase.reports.controller;

import com.jpomm.schedulerbase.reports.facade.ClientesGrandesReportFacade;
import jakarta.servlet.http.HttpServletRequest;
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
class ClientesGrandesReportControllerTest {

    @Mock
    private ClientesGrandesReportFacade facade;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ClientesGrandesReportController controller;

    @Test
    void listUnitsWithoutTransmission3DaysReturnsFacadeResponse() {
        final List<Map<String, Object>> expected = List.of(Map.of("nCodUni", 24794, "cRef", "UVI-24794"));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports/clientes-grandes/sin-trans-3dias");
        when(request.getRemoteAddr()).thenReturn("10.0.0.15");
        when(facade.listUnitsWithoutTransmission3Days()).thenReturn(expected);

        final List<Map<String, Object>> actual = controller.listUnitsWithoutTransmission3Days(25, request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listUnitsWithoutTransmission3Days();
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }
}
