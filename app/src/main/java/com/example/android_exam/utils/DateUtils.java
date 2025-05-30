package com.example.android_exam.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MMM dd", Locale.getDefault());

    public static String getTodayString() {
        return DATE_FORMAT.format(new Date());
    }

    public static List<String> getLast5Days() {
        return getLastNDays(5);
    }

    public static List<String> getLast2Days(){
        return getLastNDays(2);
    }

    private static List<String> getLastNDays(int n) {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < n; i++) {
            Calendar temp = (Calendar) calendar.clone();
            temp.add(Calendar.DAY_OF_MONTH, -i);
            days.add(DATE_FORMAT.format(temp.getTime()));
        }
        return days;
    }

    public static String formatDateForDisplay(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DISPLAY_FORMAT.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }

    public static int getDaysUntilExpiry(String expiryDate) {
        try {
            Date expiry = DATE_FORMAT.parse(expiryDate);
            Date today = new Date();
            long diffInMillies = expiry.getTime() - today.getTime();
            return (int) (diffInMillies / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isExpiringSoon(String expiryDate, int daysAhead) {
        int daysUntilExpiry = getDaysUntilExpiry(expiryDate);
        return daysUntilExpiry <= daysAhead && daysUntilExpiry >= 0;
    }

    public static boolean isExpired(String expiryDate) {
        return getDaysUntilExpiry(expiryDate) < 0;
    }
}