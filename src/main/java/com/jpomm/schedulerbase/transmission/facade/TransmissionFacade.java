package com.jpomm.schedulerbase.transmission.facade;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;

import java.util.List;

public interface TransmissionFacade {
    List<TransmissionResponse> listTransmissions();
}
