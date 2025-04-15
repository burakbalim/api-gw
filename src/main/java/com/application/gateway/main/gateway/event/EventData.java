package com.application.gateway.main.gateway.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventData<T> {

    private T data;

}
