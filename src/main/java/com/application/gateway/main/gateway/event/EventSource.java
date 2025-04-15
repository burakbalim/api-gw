package com.application.gateway.main.gateway.event;

import com.application.gateway.common.util.ExecutorsProvider;
import com.application.gateway.main.common.HttpInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Event source to perform payload asynchronously using HTTP information (HttpInfo.class).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventSource {

    private final Set<EventHandler> eventHandlerList = new HashSet<>();

    private final ExecutorsProvider executorsProvider;

    private ExecutorService executorService;

    /**
     * Initializes the EventSource by obtaining the ExecutorService from the ExecutorsProvider.
     */
    @PostConstruct
    public void init() {
        this.executorService = executorsProvider.getExecutors();
    }

    /**
     * Fires an event to all subscribers asynchronously.
     *
     * @param httpInfo The HTTP information payload to be sent with the event.
     */
    public void fire(HttpInfo httpInfo) {
        if (!eventHandlerList.isEmpty()) {
            executorService.submit(() -> new GwTask(httpInfo, eventHandlerList));
        }
    }

    /**
     * Subscribes an EventHandler to receive event payloads.
     *
     * @param eventHandler The EventHandler to subscribe.
     */
    public synchronized void subscribe(EventHandler eventHandler) {
        if (eventHandlerList.stream().noneMatch(item -> item.getClass().equals(eventHandler.getClass()))) {
            eventHandlerList.add(eventHandler);
        }
    }

    /**
     * Unsubscribes an EventHandler.
     *
     * @param eventHandler The EventHandler to unsubscribe.
     */
    public synchronized void unSubscribe(EventHandler eventHandler) {
        eventHandlerList.remove(eventHandler);
    }
}