package com.jpomm.schedulerbase.notifications.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EventEmailDispatchRequest(
        @JsonAlias({"razTra", "p_nRazTra", "pNRazTra", "razonTransmision"})
        Integer nRazTra
) {
}
