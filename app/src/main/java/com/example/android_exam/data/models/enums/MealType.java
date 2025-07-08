package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum MealType implements Serializable {

    @SerializedName("breakfast")
    BREAKFAST,

    @SerializedName("lunch")
    LUNCH,

    @SerializedName("snack")
    SNACK,

    @SerializedName("dinner")
    DINNER,

    @SerializedName("other")
    OTHER;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case BREAKFAST -> "Bữa Sáng";
            case LUNCH -> "Bữa Trưa";
            case SNACK -> "Bữa Chiều";
            case DINNER -> "Bữa Tối";
            case OTHER -> "Khác";
        };
    }

    public static MealType parseMealType(String mealTypeString) {
        if (mealTypeString == null) {
            throw new IllegalArgumentException("Meal type string is null");
        }

        return switch (mealTypeString.trim().toLowerCase()) {
            case "breakfast", "bữa sáng", "bua sang" -> BREAKFAST;
            case "lunch", "bữa trưa", "bua trua" -> LUNCH;
            case "snack", "bữa chiều", "bua chieu" -> SNACK;
            case "dinner", "bữa tối", "bua toi" -> DINNER;
            case "other", "khác", "khac" -> OTHER;
            default -> throw new IllegalArgumentException("Unknown meal type: " + mealTypeString);
        };
    }
}
