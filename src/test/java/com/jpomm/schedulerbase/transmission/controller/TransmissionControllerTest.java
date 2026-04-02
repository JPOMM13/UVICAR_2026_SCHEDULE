package com.jpomm.schedulerbase.transmission.controller;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.facade.TransmissionFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransmissionControllerTest {

    @Mock
    private TransmissionFacade facade;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TransmissionController controller;

    @Test
    void listReturnsFacadeResponse() {
        final List<TransmissionResponse> expected = List.of(new TransmissionResponse(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                LocalDateTime.of(2026, 4, 2, 9, 59),
                LocalDateTime.of(2026, 4, 2, 9, 59, 30),
                BigDecimal.ONE,
                BigDecimal.TEN,
                50,
                180,
                3200,
                "U100",
                100,
                "ENT",
                "SAL",
                "D1"
        ));
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/transmissions");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(facade.listTransmissions()).thenReturn(expected);

        final List<TransmissionResponse> actual = controller.list(request);

        assertThat(actual).isSameAs(expected);
        verify(facade).listTransmissions();
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(request).getRemoteAddr();
    }
}
