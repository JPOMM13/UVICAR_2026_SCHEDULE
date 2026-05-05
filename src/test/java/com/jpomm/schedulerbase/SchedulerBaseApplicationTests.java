package com.jpomm.schedulerbase;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootTest(properties = {
        "spring.cloud.azure.keyvault.secret.property-source-enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
class SchedulerBaseApplicationTests {

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }
}
