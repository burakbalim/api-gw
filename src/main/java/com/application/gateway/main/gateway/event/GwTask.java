package com.application.gateway.main.gateway.event;

import com.application.gateway.common.HttpInputType;
import com.application.gateway.main.common.HttpInfo;
import com.application.gateway.main.common.RequestInfoBase;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class GwTask implements Runnable {

    private final HttpInfo httpInfo;

    private final Set<EventHandler> eventHandlerList;

    GwTask(HttpInfo httpInfo, Set<EventHandler> eventHandlerList) {
        this.eventHandlerList = eventHandlerList;
        this.httpInfo = httpInfo;
    }

    @Override
    public void run() {
        EventData<HttpData> eventData = getEventData();
        eventHandlerList.forEach(handler -> {
            try {
                handler.handle(eventData);
            } catch (Exception e) {
                log.error("Occurred exception while handling event for handler: {}", handler.getClass(), e);
            }
        });
    }

    private EventData<HttpData> getEventData() {
        EventData<HttpData> event = new EventData<>();
        HttpData httpData = new HttpData();
        httpData.setType(httpInfo instanceof RequestInfoBase ? HttpInputType.REQUEST : HttpInputType.RESPONSE);
        httpData.setUri(httpInfo.getUri());
        httpData.setHttpMethod(httpInfo.getHttpMethod().toString());
        httpData.setHeaders(httpInfo.getHeaders());
        event.setData(httpData);
        return event;
    }
}
