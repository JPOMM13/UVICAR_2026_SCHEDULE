package com.jpomm.schedulerbase.transmission.service;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.repository.TransmissionQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TransmissionQueryServiceImpl implements TransmissionQueryService {

    private final TransmissionQueryRepository repository;

    public TransmissionQueryServiceImpl(final TransmissionQueryRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public List<TransmissionResponse> listTransmissions() {
        return repository.listLatestTransmissions();
    }
}
