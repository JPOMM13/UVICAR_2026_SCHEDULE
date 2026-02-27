package com.jpomm.schedulerbase.jobs;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class SampleScheduledJobs {

    private static final Logger log = LoggerFactory.getLogger(SampleScheduledJobs.class);

    // Every 5 minutes
    @Scheduled(cron = "${jobs.sample.every5min-cron:0 */5 * * * *}", zone = "${jobs.timezone:America/Lima}")
    @SchedulerLock(name = "job_every5min", lockAtMostFor = "PT4M", lockAtLeastFor = "PT5S")
    public void runEvery5Minutes() {
        log.info("[every5min] Running at {}", OffsetDateTime.now());
        // TODO: your logic here
    }

    // Twice a day at 09:00 and 18:00
    @Scheduled(cron = "${jobs.sample.twiceDaily-cron:0 0 9,18 * * *}", zone = "${jobs.timezone:America/Lima}")
    @SchedulerLock(name = "job_twiceDaily", lockAtMostFor = "PT30M", lockAtLeastFor = "PT5S")
    public void runTwiceDaily() {
        log.info("[twiceDaily] Running at {}", OffsetDateTime.now());
        // TODO: your logic here
    }
}
