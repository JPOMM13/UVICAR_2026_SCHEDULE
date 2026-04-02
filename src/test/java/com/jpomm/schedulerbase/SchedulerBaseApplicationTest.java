package com.jpomm.schedulerbase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerBaseApplicationTest {

    @Test
    void applicationClassKeepsExpectedAnnotations() {
        assertThat(SchedulerBaseApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
        assertThat(SchedulerBaseApplication.class.isAnnotationPresent(EnableScheduling.class)).isTrue();
    }
}
