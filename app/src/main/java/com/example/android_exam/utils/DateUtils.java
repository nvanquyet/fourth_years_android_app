package com.example.android_exam.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static boolean isExpired(Date date) {
        return date != null && date.before(new Date());
    }
}
