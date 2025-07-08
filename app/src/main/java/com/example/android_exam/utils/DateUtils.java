package com.example.android_exam.utils;

import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private static final String TAG = "DateUtils";

    // Timezone constants
    private static final ZoneId SYSTEM_TIMEZONE = ZoneId.systemDefault();
    private static final ZoneId UTC_TIMEZONE = ZoneOffset.UTC;

    // Date formatters - thread-safe and reusable
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault());
    private static final DateTimeFormatter DD_MM_YYYY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter ISO_UTC_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // ISO DateTime formatters
    private static final DateTimeFormatter FORMATTER_WITH_MICROSECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    private static final DateTimeFormatter FORMATTER_WITH_MILLISECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter FORMATTER_WITHOUT_MILLISECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Get today's date as string in system timezone
     */
    public static String getTodayString() {
        return LocalDate.now(SYSTEM_TIMEZONE).format(DATE_FORMAT);
    }

    /**
     * Get today's date as string in UTC timezone
     */
    public static String getTodayStringUTC() {
        return LocalDate.now(UTC_TIMEZONE).format(DATE_FORMAT);
    }

    /**
     * Get last 5 days from today
     */
    public static List<String> getLast5Days() {
        return getLastNDays(5);
    }

    /**
     * Get last 2 days from today
     */
    public static List<String> getLast2Days() {
        return getLastNDays(2);
    }

    /**
     * Get last N days from today in system timezone
     */
    private static List<String> getLastNDays(int n) {
        List<String> days = new ArrayList<>(n);
        LocalDate today = LocalDate.now(SYSTEM_TIMEZONE);

        for (int i = 0; i < n; i++) {
            LocalDate date = today.minusDays(i);
            days.add(date.format(DATE_FORMAT));
        }
        return days;
    }

    /**
     * Format date string for display (MMM dd format)
     */
    public static String formatDateForDisplay(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DATE_FORMAT);
            return date.format(DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            Log.w(TAG, "Failed to parse date for display: " + dateString, e);
            return dateString;
        }
    }

    /**
     * Get days until expiry (positive = future, negative = past)
     */
    public static int getDaysUntilExpiry(String expiryDate) {
        try {
            LocalDate expiry = LocalDate.parse(expiryDate, DATE_FORMAT);
            LocalDate today = LocalDate.now(SYSTEM_TIMEZONE);
            return (int) ChronoUnit.DAYS.between(today, expiry);
        } catch (DateTimeParseException e) {
            Log.w(TAG, "Failed to parse expiry date: " + expiryDate, e);
            return 0;
        }
    }

    /**
     * Check if date is expiring soon (within daysAhead days)
     */
    public static boolean isExpiringSoon(String expiryDate, int daysAhead) {
        int daysUntilExpiry = getDaysUntilExpiry(expiryDate);
        return daysUntilExpiry <= daysAhead && daysUntilExpiry >= 0;
    }

    /**
     * Check if date is expired
     */
    public static boolean isExpired(String expiryDate) {
        return getDaysUntilExpiry(expiryDate) < 0;
    }

    /**
     * Format Date to dd/MM/yyyy string
     */
    public static String formatDate(Date date) {
        if (date == null) return "";

        LocalDate localDate = convertToLocalDate(date);
        return localDate.format(DD_MM_YYYY_FORMAT);
    }

    /**
     * Format Date to ISO 8601 string in UTC (for API calls)
     * Converts local time to UTC before formatting
     */
    public static String formatDateTimeToIso(Date dateTime) {
        if (dateTime == null) {
            dateTime = new Date();
        }

        // Convert to UTC and format with Z suffix
        ZonedDateTime utcDateTime = dateTime.toInstant().atZone(UTC_TIMEZONE);
        return utcDateTime.format(ISO_UTC_FORMAT);
    }

    /**
     * Format LocalDate to ISO 8601 string (at start of day)
     */
    public static String formatDateTimeToIso(LocalDate dateTime) {
        if (dateTime == null) {
            return LocalDateTime.now(SYSTEM_TIMEZONE).format(ISO_FORMAT);
        }

        return dateTime.atStartOfDay().format(ISO_FORMAT);
    }

    /**
     * NEW: Format LocalDateTime to ISO 8601 string in UTC (for API calls)
     * This method treats the LocalDateTime as if it's in the system timezone
     * and converts it to UTC before formatting
     */
    public static String formatLocalDateTimeToIsoUTC(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            localDateTime = LocalDateTime.now(SYSTEM_TIMEZONE);
        }

        // Treat the LocalDateTime as being in the system timezone
        ZonedDateTime zonedDateTime = localDateTime.atZone(SYSTEM_TIMEZONE);
        // Convert to UTC and format with Z suffix
        ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(UTC_TIMEZONE);
        return utcDateTime.format(ISO_UTC_FORMAT);
    }

    /**
     * NEW: Create LocalDateTime from date/time components in system timezone
     */
    public static LocalDateTime createLocalDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
        return LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute, 0);
    }

    /**
     * Parse ISO 8601 date string to LocalDateTime
     * Supports multiple formats with/without microseconds/milliseconds
     * If the string has Z suffix, it's treated as UTC and converted to local time
     */
    public static LocalDateTime parseIsoDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        String originalString = dateString.trim();
        boolean isUtc = originalString.endsWith("Z");
        String cleanDateString = originalString.replace("Z", "");

        try {
            LocalDateTime localDateTime;

            // Try with fractional seconds
            if (cleanDateString.contains(".")) {
                String[] parts = cleanDateString.split("\\.");
                String fractionalPart = parts[1];

                if (fractionalPart.length() >= 6) {
                    // Microseconds - truncate to 6 digits
                    cleanDateString = parts[0] + "." + fractionalPart.substring(0, 6);
                    localDateTime = LocalDateTime.parse(cleanDateString, FORMATTER_WITH_MICROSECONDS);
                } else if (fractionalPart.length() >= 1) {
                    // Milliseconds - pad to 3 digits
                    String milliseconds = String.format("%-3s", fractionalPart).replace(' ', '0');
                    if (milliseconds.length() > 3) {
                        milliseconds = milliseconds.substring(0, 3);
                    }
                    cleanDateString = parts[0] + "." + milliseconds;
                    localDateTime = LocalDateTime.parse(cleanDateString, FORMATTER_WITH_MILLISECONDS);
                } else {
                    // Remove fractional part if it's empty
                    cleanDateString = parts[0];
                    localDateTime = LocalDateTime.parse(cleanDateString, FORMATTER_WITHOUT_MILLISECONDS);
                }
            } else {
                // Try without fractional seconds
                localDateTime = LocalDateTime.parse(cleanDateString, FORMATTER_WITHOUT_MILLISECONDS);
            }

            // If the original string had Z suffix, convert from UTC to local time
            if (isUtc) {
                ZonedDateTime utcDateTime = localDateTime.atZone(UTC_TIMEZONE);
                ZonedDateTime localZonedDateTime = utcDateTime.withZoneSameInstant(SYSTEM_TIMEZONE);
                return localZonedDateTime.toLocalDateTime();
            }

            return localDateTime;

        } catch (DateTimeParseException e) {
            Log.e(TAG, "Failed to parse ISO date: " + dateString, e);
            return null;
        }
    }
    public static LocalDateTime parseIsoDateTimeWithoutTimezoneConvert(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove Z suffix and parse
            String cleanDateString = dateString.trim().replace("Z", "");

            // Handle both with and without milliseconds
            LocalDateTime localDateTime;
            if (cleanDateString.contains(".")) {
                // With milliseconds/microseconds - just take first 3 digits
                String[] parts = cleanDateString.split("\\.");
                String fractionalPart = parts[1];
                if (fractionalPart.length() > 3) {
                    fractionalPart = fractionalPart.substring(0, 3);
                }
                cleanDateString = parts[0] + "." + fractionalPart;
                localDateTime = LocalDateTime.parse(cleanDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            } else {
                localDateTime = LocalDateTime.parse(cleanDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }

            // Return LocalDateTime object
            return localDateTime;

        } catch (DateTimeParseException e) {
            Log.e(TAG, "Failed to parse ISO date: " + dateString, e);
            return null;
        }
    }


    /**
     * Parse dd/MM/yyyy string to Date
     */
    public static Date parseIsoDateToDate(String dateString) {
        Log.d(TAG, "Parsing date string: " + dateString);

        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            // First try to parse as ISO datetime
            LocalDateTime dateTime = parseIsoDateTime(dateString);
            if (dateTime != null) {
                return Date.from(dateTime.atZone(SYSTEM_TIMEZONE).toInstant());
            }

            // If that fails, try dd/MM/yyyy format
            LocalDate localDate = LocalDate.parse(dateString, DD_MM_YYYY_FORMAT);
            return convertToDate(localDate);
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Failed to parse date: " + dateString, e);
            return null;
        }
    }

    /**
     * Format LocalDateTime to ISO 8601 string with UTC offset
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.atOffset(ZoneOffset.UTC).toString();
    }

    /**
     * Convert LocalDate to LocalDateTime at start of day
     */
    public static LocalDateTime parseLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay();
    }

    /**
     * Convert Date to LocalDateTime in system timezone
     */
    public static LocalDateTime parseLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(SYSTEM_TIMEZONE).toLocalDateTime();
    }

    /**
     * Convert Date to LocalDate in system timezone
     */
    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(SYSTEM_TIMEZONE).toLocalDate();
    }

    /**
     * Convert LocalDate to Date (at start of day in system timezone)
     */
    public static Date convertToDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(SYSTEM_TIMEZONE).toInstant());
    }

    /**
     * Get current timestamp in UTC
     */
    public static long getCurrentTimestampUTC() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Get current date in specific timezone
     */
    public static LocalDate getCurrentDate(ZoneId zoneId) {
        return LocalDate.now(zoneId != null ? zoneId : SYSTEM_TIMEZONE);
    }

    /**
     * Get current date time in specific timezone
     */
    public static LocalDateTime getCurrentDateTime(ZoneId zoneId) {
        return LocalDateTime.now(zoneId != null ? zoneId : SYSTEM_TIMEZONE);
    }

    /**
     * Format date with custom pattern and timezone
     */
    public static String formatDateWithPattern(LocalDate date, String pattern, ZoneId zoneId) {
        if (date == null || pattern == null) return "";

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return date.format(formatter);
        } catch (Exception e) {
            Log.e(TAG, "Failed to format date with pattern: " + pattern, e);
            return "";
        }
    }

    /**
     * Calculate age from birth date
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now(SYSTEM_TIMEZONE));
    }

    /**
     * Check if a date is today
     */
    public static boolean isToday(LocalDate date) {
        if (date == null) return false;
        return date.isEqual(LocalDate.now(SYSTEM_TIMEZONE));
    }

    /**
     * Check if a date is yesterday
     */
    public static boolean isYesterday(LocalDate date) {
        if (date == null) return false;
        return date.isEqual(LocalDate.now(SYSTEM_TIMEZONE).minusDays(1));
    }

    /**
     * Check if a date is tomorrow
     */
    public static boolean isTomorrow(LocalDate date) {
        if (date == null) return false;
        return date.isEqual(LocalDate.now(SYSTEM_TIMEZONE).plusDays(1));
    }

    /**
     * Get relative date string (Today, Yesterday, Tomorrow, or formatted date)
     */
    public static String getRelativeDateString(LocalDate date) {
        if (date == null) return "";

        if (isToday(date)) {
            return "Today";
        } else if (isYesterday(date)) {
            return "Yesterday";
        } else if (isTomorrow(date)) {
            return "Tomorrow";
        } else {
            return date.format(DISPLAY_FORMAT);
        }
    }
}