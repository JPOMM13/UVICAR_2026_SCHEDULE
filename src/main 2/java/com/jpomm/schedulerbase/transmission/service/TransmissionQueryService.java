package com.jpomm.schedulerbase.transmission.service;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;

import java.util.List;

public interface TransmissionQueryService {
    List<TransmissionResponse> listTransmissions();
}
