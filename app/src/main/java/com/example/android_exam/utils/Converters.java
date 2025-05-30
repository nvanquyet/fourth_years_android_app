package com.example.android_exam.utils;

import androidx.room.TypeConverter;
import com.example.android_exam.data.local.entity.MealType;

public class Converters {
    @TypeConverter
    public static MealType fromString(String value) {
        return value == null ? null : MealType.valueOf(value);
    }

    @TypeConverter
    public static String mealTypeToString(MealType mealType) {
        return mealType == null ? null : mealType.name();
    }
}