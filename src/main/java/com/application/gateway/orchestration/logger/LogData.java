package com.application.gateway.orchestration.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.logging.Level;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogData {

    private Level level;

    private String text;
}
