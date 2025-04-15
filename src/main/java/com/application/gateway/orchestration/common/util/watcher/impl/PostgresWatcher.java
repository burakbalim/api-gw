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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

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

    private Connection connection;
    private final static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
    private final Set<String> listenedTables = new HashSet<>();

    @Override
    protected void onInit() {
        pathToServiceMap.forEach((configurationSourceDTO, configurable) -> setup(configurationSourceDTO.getConfigurationSource()));
        pathToServiceMap.forEach((configurationSourceDTO, configurable) -> listenedTables.add(configurationSourceDTO.getName()));
        initializeListener();
    }


    private void initializeListener() {
        try {
            DataSource dataSource = jdbcTemplate.getDataSource();
            if (dataSource == null) {
                throw new IllegalStateException("DataSource is not available");
            }

            if (connection == null || connection.isClosed()) {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                log.info("Database connection established for LISTEN.");
            }

            for (String table : listenedTables) {
                connection.createStatement().execute("LISTEN " + table);
                log.info("Listening to table: {}", table);
            }
        } catch (Exception e) {
            log.error("Error initializing LISTEN connection", e);
        }
    }

    @Override
    public void onListen() {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::listen, 1, 10, TimeUnit.MINUTES);
    }

    private void listen() {
        try {
            PGConnection pgConnection = connection.unwrap(PGConnection.class);
            PGNotification[] notifications = pgConnection.getNotifications();
            if (notifications != null) {
                for (PGNotification notification : notifications) {
                    log.info("Received notification from {}: {}", notification.getName(), notification.getParameter());
                }
            }
        } catch (SQLException e) {
            log.error("Error checking notifications", e);
            restartListening();
        }
    }

    private void restartListening() {
        log.warn("Restarting LISTEN connection...");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Error closing connection", e);
        }

        initializeListener();
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

    private boolean functionExists(String functionName) {
        String sql = "SELECT COUNT(*) FROM pg_proc WHERE proname = ?";
        Integer count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{functionName},
                Integer.class
        );
        return count != null && count > 0;
    }

    private boolean triggerExists(String triggerName) {
        String sql = "SELECT COUNT(*) FROM pg_trigger WHERE tgname = ?";
        Integer count = jdbcTemplate.queryForObject(
                sql,
                new Object[]{triggerName},
                Integer.class
        );
        return count != null && count > 0;
    }
}
