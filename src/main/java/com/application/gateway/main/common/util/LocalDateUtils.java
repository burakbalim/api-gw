package com.application.gateway.main.common.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateUtils {

    public static LocalDate converToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
