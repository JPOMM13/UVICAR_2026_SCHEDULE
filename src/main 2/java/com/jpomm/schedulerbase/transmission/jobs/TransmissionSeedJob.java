package com.jpomm.schedulerbase.transmission.jobs;

import com.jpomm.schedulerbase.transmission.repository.TransmissionSeedRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TransmissionSeedJob {

    private static final Logger log = LoggerFactory.getLogger(TransmissionSeedJob.class);

    private final TransmissionSeedRepository seedRepository;

    /**
     * Si el job detecta un error estructural en BD (por ejemplo triggers/views desalineados),
     * se deshabilita en runtime para evitar spamear el log cada ejecución.
     */
    private final AtomicBoolean runtimeDisabled = new AtomicBoolean(false);

    @Value("${jobs.seed.enabled:true}")
    private boolean enabled;

    @Value("${jobs.seed.rows-per-run:10}")
    private int rowsPerRun;

    public TransmissionSeedJob(final TransmissionSeedRepository seedRepository) {
        this.seedRepository = Objects.requireNonNull(seedRepository);
    }

    /**
     * Job para insertar data de prueba mientras el API está corriendo.
     */
    @Scheduled(cron = "${jobs.seed.cron:*/30 * * * * *}", zone = "${jobs.timezone:America/Lima}")
    @SchedulerLock(name = "job_seed_transmissions", lockAtMostFor = "PT25S", lockAtLeastFor = "PT2S")
    public void seed() {
        if (!enabled || runtimeDisabled.get()) {
            return;
        }
        try {
            final int inserted = seedRepository.insertRandomRows(rowsPerRun);
            log.info("[seed] Inserted {} rows into tTransmisiones", inserted);
        } catch (Exception ex) {
            final String msg = ex.getMessage() != null ? ex.getMessage() : "";
            log.error("[seed] Error inserting rows into tTransmisiones: {}", msg, ex);

            // Caso típico: la BD tiene un trigger/vista que referencia una columna que no existe.
            // Ejemplo visto: "Invalid column name 'cFlagQuellaveco'".
            if (msg.contains("Invalid column name") || msg.contains("Invalid column") || msg.contains("Invalid column name")) {
                if (runtimeDisabled.compareAndSet(false, true)) {
                    log.error("[seed] Disabling seed job at runtime due to structural DB error (likely trigger/view mismatch). " +
                            "Fix the DB object and restart, or set jobs.seed.enabled=false. " +
                            "Tip: run docs/DB_DIAG.sql to locate offending triggers/views.");
                }
            }
        }
    }
}
