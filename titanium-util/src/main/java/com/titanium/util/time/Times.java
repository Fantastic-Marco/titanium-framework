package com.titanium.util.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public  class Times {
    public static final DateTimeFormatter YEAR_MOUTH_DAY_HOUR_MINUTES_SECONDS = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter YEAR_MOUTH_DAY_HOUR_MINUTES = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter YEAR_MOUTH_DAY = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");


    public static LocalDate toLocalDate(long millis) {
        return toZoneDateTime(millis).toLocalDate();
    }

    public static LocalDate toLocalDate(Date date) {
        return toZoneDateTime(date).toLocalDate();
    }

    public static LocalTime toLocalTime(long millis) {
        return toZoneDateTime(millis).toLocalTime();
    }

    public static LocalTime toLocalTime(Date date) {
        return toZoneDateTime(date).toLocalTime();
    }

    public static LocalDateTime toLocalDateTime(long millis) {
        return toZoneDateTime(millis).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return toZoneDateTime(date).toLocalDateTime();
    }


    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalTime localTime) {
        return Date.from(localTime.atDate(LocalDate.of(1970, 1, 1))
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static long toMillis(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toMillis(LocalTime localTime) {
        return localTime.atDate(LocalDate.of(1970, 1, 1))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long toMillis(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private static ZonedDateTime toZoneDateTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.systemDefault());
    }

    private static ZonedDateTime toZoneDateTime(Date date) {
        return Objects.requireNonNull(date, "date 对象不允许为空").toInstant()
                .atZone(ZoneOffset.systemDefault());
    }


    public static LocalDate toLocalDate(String dateString) {
        return LocalDate.parse(dateString, YEAR_MOUTH_DAY);
    }

    public static LocalDateTime toLocalDateTime(String dateString) {
        return LocalDateTime.parse(dateString, YEAR_MOUTH_DAY_HOUR_MINUTES_SECONDS);
    }

    /**
     * 获取当前时间到凌晨到时间差（秒）
     *
     * @return 现在到凌晨的时间（秒）
     */
    public static Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }
}
