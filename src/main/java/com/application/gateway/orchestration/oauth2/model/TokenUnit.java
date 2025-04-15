package com.application.gateway.orchestration.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class TokenUnit {

    @JsonProperty("time")
    @Field("time")
    private Long time;

    @JsonProperty("chrono_unit")
    @Field("chrono_unit")
    private String chronoUnit;

    public Duration toDuration() {
        if (time == null || chronoUnit == null) {
            throw new IllegalArgumentException("Time and chronoUnit must not be null");
        }
        ChronoUnit unit;
        try {
            unit = ChronoUnit.valueOf(chronoUnit.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid chrono unit: " + chronoUnit, e);
        }
        return Duration.of(time, unit);
    }
}
