package com.jpomm.schedulerbase.transmission.jobs;

import com.jpomm.schedulerbase.transmission.repository.TransmissionSeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransmissionSeedJobTest {

    @Mock
    private TransmissionSeedRepository seedRepository;

    @InjectMocks
    private TransmissionSeedJob job;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(job, "enabled", true);
        ReflectionTestUtils.setField(job, "rowsPerRun", 7);
    }

    @Test
    void seedSkipsWhenDisabledByConfig() {
        ReflectionTestUtils.setField(job, "enabled", false);

        job.seed();

        verifyNoInteractions(seedRepository);
    }

    @Test
    void seedInsertsConfiguredRowsWhenEnabled() {
        when(seedRepository.insertRandomRows(7)).thenReturn(7);

        job.seed();

        verify(seedRepository).insertRandomRows(7);
    }

    @Test
    void seedDisablesRuntimeAfterStructuralDatabaseError() {
        when(seedRepository.insertRandomRows(7)).thenThrow(new IllegalStateException("Invalid column name 'x'"));

        job.seed();
        job.seed();

        verify(seedRepository, times(1)).insertRandomRows(7);
    }

    @Test
    void seedDoesNotDisableRuntimeForNonStructuralError() {
        when(seedRepository.insertRandomRows(7)).thenThrow(new IllegalStateException("timeout"));

        job.seed();
        job.seed();

        verify(seedRepository, times(2)).insertRandomRows(7);
    }
}
