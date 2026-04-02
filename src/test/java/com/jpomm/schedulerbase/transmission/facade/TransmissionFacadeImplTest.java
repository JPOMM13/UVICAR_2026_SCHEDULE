package com.jpomm.schedulerbase.transmission.facade;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.service.TransmissionQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransmissionFacadeImplTest {

    @Mock
    private TransmissionQueryService queryService;

    @InjectMocks
    private TransmissionFacadeImpl facade;

    @Test
    void listTransmissionsDelegatesToService() {
        final List<TransmissionResponse> expected = List.of();
        when(queryService.listTransmissions()).thenReturn(expected);

        final List<TransmissionResponse> actual = facade.listTransmissions();

        assertThat(actual).isSameAs(expected);
        verify(queryService).listTransmissions();
    }
}
