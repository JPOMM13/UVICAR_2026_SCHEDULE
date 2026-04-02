package com.jpomm.schedulerbase.transmission.service;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.repository.TransmissionQueryRepository;
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
class TransmissionQueryServiceImplTest {

    @Mock
    private TransmissionQueryRepository repository;

    @InjectMocks
    private TransmissionQueryServiceImpl service;

    @Test
    void listTransmissionsDelegatesToRepository() {
        final List<TransmissionResponse> expected = List.of();
        when(repository.listLatestTransmissions()).thenReturn(expected);

        final List<TransmissionResponse> actual = service.listTransmissions();

        assertThat(actual).isSameAs(expected);
        verify(repository).listLatestTransmissions();
    }
}
