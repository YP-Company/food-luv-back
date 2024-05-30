package com.youngpotato.foodluv.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateUtil {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_NONE_DASH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private DateUtil() {}

    public static LocalDate getToday() {
        return LocalDate.now();
    }

    public static String getTodayString() {
        return getToday().format(DATE_TIME_FORMATTER);
    }

    public static String getTodayString(String formatPattern) {
        return getToday().format(DateTimeFormatter.ofPattern(formatPattern));
    }

    public static String getDateString(LocalDate date, String formatPattern) {
        Objects.requireNonNull(date);
        return date.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    public static String dateToString(LocalDate date) {
        if (date != null) {
            return date.format(DATE_FORMATTER);
        }
        return null;
    }

    public static String dateTimeToString(LocalDateTime dateTime) {
        if (dateTime != null) {
            return dateTime.format(DATE_TIME_FORMATTER);
        }
        return null;
    }
}
