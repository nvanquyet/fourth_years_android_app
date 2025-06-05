package com.example.android_exam.utils;

import androidx.room.TypeConverter;
import com.example.android_exam.data.local.entity.ExpiryStatus;

/**
 * Room type converters for custom data types
 */
public class Converters {

    @TypeConverter
    public static String fromExpiryStatus(ExpiryStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static ExpiryStatus toExpiryStatus(String status) {
        if (status == null) return null;
        try {
            return ExpiryStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ExpiryStatus.FRESH; // Default fallback
        }
    }
}