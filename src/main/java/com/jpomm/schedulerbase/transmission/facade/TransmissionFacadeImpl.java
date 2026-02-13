package com.jpomm.schedulerbase.transmission.facade;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.service.TransmissionQueryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TransmissionFacadeImpl implements TransmissionFacade {

    private final TransmissionQueryService queryService;

    public TransmissionFacadeImpl(final TransmissionQueryService queryService) {
        this.queryService = Objects.requireNonNull(queryService);
    }

    @Override
    public List<TransmissionResponse> listTransmissions() {
        return queryService.listTransmissions();
    }
}
