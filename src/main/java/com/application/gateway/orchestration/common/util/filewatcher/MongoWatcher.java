package com.application.gateway.orchestration.common.util.filewatcher;


import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@ConditionalOnProperty(value = "configuration.provider", havingValue = "MONGO")
@RequiredArgsConstructor
public class MongoWatcher extends ConfigurationWatcherBase {

    private final MongoTemplate mongoTemplate;

    private ExecutorService executorService = null;

    @Override
    protected void onInit() {
        executorService = Executors.newFixedThreadPool(this.pathToServiceMap.size(), r -> new Thread(null, r, "MongoWatcher", 0, false));
    }

    @Override
    protected void onListen() {
        pathToServiceMap.forEach((configurationSourceDTO, configurable) -> executorService.submit(() -> watch(configurationSourceDTO)));
    }

    private void watch(ConfigurationSourceDTO<?> configurationSourceDTO) {
        while (true) {
            log.info("Listening collection for {}", configurationSourceDTO.getConfigurationSource());
            String configurationSource = configurationSourceDTO.getConfigurationSource();
            MongoCollection<Document> collection = mongoTemplate.getCollection(configurationSource);
            try (MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator()) {
                while (cursor.hasNext()) {
                    log.info("Something are changed on {} collection...", configurationSource);
                    operationDetection(cursor, configurationSourceDTO.getName());
                }
            } catch (Exception e) {
                log.info("Mongo watching error... {}", e.getMessage());
                sleepFor5Seconds();
            }
        }
    }

    private void operationDetection(MongoCursor<ChangeStreamDocument<Document>> cursor, String subscriber) {
        ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
        if (changeStreamDocument.getOperationTypeString().equals("drop")) {
            throw new RuntimeException("Collection was dropped");
        }
        notifyToSubscriber(subscriber);
    }

    private void sleepFor5Seconds() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}

