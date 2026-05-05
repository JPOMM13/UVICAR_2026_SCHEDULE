package com.jpomm.schedulerbase.notifications.jobs;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.facade.EventEmailNotificationFacade;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EventEmailNotificationJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventEmailNotificationJob.class);

    private final EventEmailNotificationFacade facade;

    @Value("${jobs.notifications.event-email.enabled:true}")
    private boolean enabled;

    public EventEmailNotificationJob(final EventEmailNotificationFacade facade) {
        this.facade = Objects.requireNonNull(facade);
    }

    @Scheduled(cron = "${jobs.notifications.event-email.cron:0 0 * * * *}", zone = "${jobs.timezone:America/Lima}")
    @SchedulerLock(name = "job_event_email_notifications", lockAtMostFor = "PT55M", lockAtLeastFor = "PT5S")
    public void dispatchPendingEventEmails() {
        if (!enabled) {
            return;
        }

        final EventEmailJobResult result = facade.processPendingNotifications();
        LOGGER.info("[event-email] Job finished. fetchedCount={}, sentCount={}", result.fetchedCount(), result.sentCount());
    }
}
