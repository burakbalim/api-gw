package com.application.gateway.main.gateway.event;

import com.application.gateway.orchestration.ConfigurationBaseDTO;
import com.application.gateway.orchestration.OrchestrationBase;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;

public abstract class EventHandlerBase<T extends ConfigurationBaseDTO, K> extends OrchestrationBase<T, K> implements EventHandler {

    EventSource eventSource;

    protected EventHandlerBase(EventSource eventSource, ConfigurationProvider<T> configurationProvider) {
        super(configurationProvider);
        this.eventSource = eventSource;
    }

    protected void notifyToEventSource(boolean enabled) {
        if (enabled) {
            eventSource.subscribe(this);
        } else {
            eventSource.unSubscribe(this);
        }
    }
}
