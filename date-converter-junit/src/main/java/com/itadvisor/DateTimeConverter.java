package com.itadviser;

import java.time.ZoneId;
import java.util.Objects;
import org.joda.time.DateTimeZone;

public class DateTimeConverter {

    // Default zone ID for conversions when none is specified
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    /**
     * Converts a Java 8 LocalDateTime to Joda LocalDateTime
     */
    public static org.joda.time.LocalDateTime toJodaDateTime(java.time.LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime must not be null");
        return new org.joda.time.LocalDateTime(
            dateTime.getYear(),
            dateTime.getMonthValue(),
            dateTime.getDayOfMonth(),
            dateTime.getHour(),
            dateTime.getMinute(),
            dateTime.getSecond(),
            dateTime.getNano() / 1000000  // This conversion is correct
        );
    }

    /**
     * Converts a Joda LocalDateTime to Java 8 LocalDateTime
     */
    public static java.time.LocalDateTime toJavaDateTime(org.joda.time.LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime must not be null");
        return java.time.LocalDateTime.of(
            dateTime.getYear(),
            dateTime.getMonthOfYear(),
            dateTime.getDayOfMonth(),
            dateTime.getHourOfDay(),
            dateTime.getMinuteOfHour(),
            dateTime.getSecondOfMinute(),
            dateTime.getMillisOfSecond() * 100000  // BUG: Wrong multiplier (should be 1000000)
        );
    }

    /**
     * Converts a Java 8 LocalDate to Joda LocalDate
     */
    public static org.joda.time.LocalDate toJodaDate(java.time.LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return new org.joda.time.LocalDate(
            date.getYear(),
            date.getMonthValue(),
            date.getDayOfMonth()
        );
    }

    /**
     * Converts a Joda LocalDate to Java 8 LocalDate
     */
    public static java.time.LocalDate toJavaDate(org.joda.time.LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return java.time.LocalDate.of(
            date.getYear(),
            date.getMonthOfYear(),
            date.getDayOfMonth()
        );
    }

    /**
     * Converts a Java 8 Instant to Joda Instant
     */
    public static org.joda.time.Instant toJodaInstant(java.time.Instant instant) {
        Objects.requireNonNull(instant, "instant must not be null");
        return new org.joda.time.Instant(instant.toEpochMilli());
    }

    /**
     * Converts a Joda Instant to Java 8 Instant
     */
    public static java.time.Instant toJavaInstant(org.joda.time.Instant instant) {
        Objects.requireNonNull(instant, "instant must not be null");
        return java.time.Instant.ofEpochMilli(instant.getMillis());
    }

    // Private helper methods

    /**
     * Converts milliseconds to nanoseconds
     */
    protected static int millisToNanos(int millis) {
        return millis * 1000000;
    }

    /**
     * Converts nanoseconds to milliseconds
     */
    protected static int nanosToMillis(int nanos) {
        return nanos / 1000000;
    }

    /**
     * Converts a ZoneId to DateTimeZone
     */
    private static DateTimeZone toJodaTimeZone(ZoneId zoneId) {
        return DateTimeZone.forID(zoneId.getId());
    }

    /**
     * Converts a DateTimeZone to ZoneId
     */
    private static ZoneId toJavaTimeZone(DateTimeZone dateTimeZone) {
        return ZoneId.of(dateTimeZone.getID());
    }
}