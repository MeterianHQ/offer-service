package com.ovoenergy.offer.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@UtilityClass
public class DateUtils {

    public static Long getCurrentTimeMidnightMilliseconds(Instant instant) {
        return LocalDateTime.of(instant.atZone(ZoneId.of("UTC")).toLocalDate(), LocalTime.MIDNIGHT)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

    public static Long getCurrentTimeEndOfDay(Long time) {
        return LocalDateTime.of(Instant.ofEpochMilli(time).atZone(ZoneId.of("UTC")).toLocalDate(), LocalTime.MAX)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }
}
