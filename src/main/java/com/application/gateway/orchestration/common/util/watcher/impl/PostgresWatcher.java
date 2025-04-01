package com.application.gateway.orchestration.common.util.watcher.impl;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.util.watcher.ConfigurationWatcherBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "configuration.provider", havingValue = "POSTGRES")
@EnableScheduling
public class PostgresWatcher extends ConfigurationWatcherBase {

    private static final String FUNCTION_TEMPLATE = """
            CREATE OR REPLACE TRIGGER %s_watcher
            AFTER INSERT OR UPDATE ON %s
            FOR EACH ROW EXECUTE FUNCTION notify_on_change_%s();
            """;

    private static final String TRIGGER_TEMPLATE = """
            CREATE OR REPLACE FUNCTION notify_on_change_%s()
            RETURNS TRIGGER AS $$
            BEGIN
                PERFORM pg_notify('%s',
                JSON_BUILD_OBJECT(
                    'operation', TG_OP,
                    'id', NEW.id
                )::text);
                RETURN NEW;
            END;
            $$ LANGUAGE plpgsql;
            """;


    private final JdbcTemplate jdbcTemplate;

    @Override
    protected void onInit() {
        pathToServiceMap.forEach((configurationSourceDTO, configurable) -> setup(configurationSourceDTO.getConfigurationSource()));
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Override
    protected void onListen() {
        pathToServiceMap.forEach((configurationSourceDTO, configurable) -> {
            createNotificationHandler(configurationSourceDTO);
        });
    }

    private void setup(String configurationSource) {
        String triggerFunction = String.format(TRIGGER_TEMPLATE, configurationSource, configurationSource);
        String trigger = String.format(FUNCTION_TEMPLATE, configurationSource, configurationSource, configurationSource);

        if (!functionExists("notify_on_change_" + configurationSource)) {
            log.info("setting up function for {}", configurationSource);
            jdbcTemplate.execute(triggerFunction);
        }

        if (!triggerExists(configurationSource + "_watcher")) {
            log.info("setting up trigger for {} ", configurationSource);
            jdbcTemplate.execute(trigger);
        }
    }

    public void createNotificationHandler(ConfigurationSourceDTO<?> configurationSourceDTO) {
        try {
            jdbcTemplate.execute((Connection c) -> {
                c.setAutoCommit(false);
                String tableName = configurationSourceDTO.getConfigurationSource();
                log.info("Listening table {}", tableName);
                c.createStatement().execute("LISTEN " + tableName);
                PGConnection pgconn = c.unwrap(PGConnection.class);
                PGNotification[] nts = pgconn.getNotifications(1000);
                if (nts == null || nts.length == 0) {
                    return 0;
                }
                log.info("Something changed on {}", tableName);
                notifyToSubscriber(configurationSourceDTO.getName());
                return 0;
            });
        } catch (Exception e) {
            log.error("Occurred exception on listening {}", configurationSourceDTO.getConfigurationSource(), e);
        }
    }

    public boolean functionExists(String functionName) {
        String sql = "SELECT COUNT(*) FROM pg_proc WHERE proname = ?";
        Integer count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{functionName},
                Integer.class
        );
        return count != null && count > 0;
    }

    public boolean triggerExists(String triggerName) {
        String sql = "SELECT COUNT(*) FROM pg_trigger WHERE tgname = ?";
        Integer count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{triggerName},
                Integer.class
        );
        return count != null && count > 0;
    }
}
