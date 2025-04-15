package com.application.gateway.orchestration.common.util.watcher.impl;

import com.application.gateway.orchestration.common.util.watcher.ConfigurationWatcherBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
@Slf4j
@ConditionalOnProperty(value = "configuration.provider", havingValue = "FILE")
public class FileWatcher extends ConfigurationWatcherBase {

    @Override
    protected void onListen() {
        this.listen();
    }

    @Override
    protected void onInit() {
        /**
         * Do nothing
         */
    }


    private void listen() {
        WatchService watcher = getWatcher();
        registerPaths(watcher);
        try {
            while (true) {
                if (watching(watcher)) break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private boolean watching(WatchService watcher) throws InterruptedException {
        WatchKey key = watcher.take();
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                notifyToSubscriber(event.context().toString());
            }
        }
        return !key.reset();
    }

    private void registerPaths(WatchService watcher) {
        pathToServiceMap.keySet().forEach(pathKey -> {
            Path path = Paths.get(pathKey.getConfigurationSource());
            try {
                path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            } catch (IOException ignored) {
                log.error("", ignored);
            }
        });
    }

    private static WatchService getWatcher() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
