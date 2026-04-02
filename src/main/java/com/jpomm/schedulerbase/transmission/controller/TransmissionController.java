package com.jpomm.schedulerbase.transmission.controller;

import com.jpomm.schedulerbase.transmission.dto.TransmissionResponse;
import com.jpomm.schedulerbase.transmission.facade.TransmissionFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/transmissions")
public class TransmissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransmissionController.class);

    private final TransmissionFacade facade;

    public TransmissionController(final TransmissionFacade facade) {
        this.facade = Objects.requireNonNull(facade);
    }

    /**
     * GET /api/transmissions
     * No recibe parámetros: la ventana de tiempo y el TOP se calculan en la consulta
     * con base a la hora actual del servidor SQL.
     */
    @GetMapping
    public List<TransmissionResponse> list(final HttpServletRequest request) {
        LOGGER.info("Consumo detectado del API [{} {}] desde {}", request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr());
        return facade.listTransmissions();
    }
}
