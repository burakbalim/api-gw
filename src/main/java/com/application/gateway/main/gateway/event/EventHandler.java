package com.application.gateway.main.gateway.event;

public interface EventHandler {

    <T> void handle(EventData<T> eventData);

}
