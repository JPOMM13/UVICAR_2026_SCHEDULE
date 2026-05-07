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
class UnidadesSinTransmisionReportControllerTest {

    @Mock
    private ClientesGrandesReportFacade facade;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UnidadesSinTransmisionReportController controller;

    @Test
    void listMtcOsinergUnitsWithoutTransmission15MinutesTo2HoursReturnsFacadeResponse() {
        final List<Map<String, Object>> expected = List.of(Map.of(
                "Placa", "BTM021",
                "nCodUni", 28951,
                "Ultima_Transmision", "2026-05-07 09:55:03.000"
        ));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports/unidades/sin-transm-mtcosinrg-15min-a-2hrs");
        when(request.getRemoteAddr()).thenReturn("10.0.0.15");
        when(facade.listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours()).thenReturn(expected);

        final List<Map<String, Object>> actual =
                controller.listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours(request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listMtcOsinergUnitsWithoutTransmission15MinutesTo2Hours();
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }

    @Test
    void listMtcOsinergUnitsWithoutTransmission2HoursTo2DaysReturnsFacadeResponse() {
        final List<Map<String, Object>> expected = List.of(Map.of(
                "Placa", "BTM021",
                "nCodUni", 28951,
                "Ultima_Transmision", "2026-05-07 07:55:03.000"
        ));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports/unidades/sin-transm-mtcosinrg-2hrs-a-2dias");
        when(request.getRemoteAddr()).thenReturn("10.0.0.15");
        when(facade.listMtcOsinergUnitsWithoutTransmission2HoursTo2Days()).thenReturn(expected);

        final List<Map<String, Object>> actual =
                controller.listMtcOsinergUnitsWithoutTransmission2HoursTo2Days(request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listMtcOsinergUnitsWithoutTransmission2HoursTo2Days();
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }

    @Test
    void listMtcOsinergUnitsWithoutTransmissionMoreThan2DaysReturnsFacadeResponse() {
        final List<Map<String, Object>> expected = List.of(Map.of(
                "Placa", "BTM021",
                "nCodUni", 28951,
                "Ultima_Transmision", "2026-05-05 07:55:03.000"
        ));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports/unidades/sin-transm-mtcosinrg-mas-de-2dias");
        when(request.getRemoteAddr()).thenReturn("10.0.0.15");
        when(facade.listMtcOsinergUnitsWithoutTransmissionMoreThan2Days()).thenReturn(expected);

        final List<Map<String, Object>> actual =
                controller.listMtcOsinergUnitsWithoutTransmissionMoreThan2Days(request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listMtcOsinergUnitsWithoutTransmissionMoreThan2Days();
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }
}
